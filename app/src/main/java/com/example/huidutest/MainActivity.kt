package com.example.huidutest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.core.SystemUIAccessor
import com.example.core.SystemUIManagement
import com.example.huidutest.databinding.ActivityMainBinding
import com.example.huidutest.lib.KioskDeviceAdminReceiver
import com.example.huidutest.lib.RootHelper


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var systemUIManagement: SystemUIManagement
    private var localDeviceAdminReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        systemUIManagement = SystemUIManagement(this, KioskDeviceAdminReceiver::class.java)

        initEvent()
        updateUI()
        registerReceiver()
        println("root:${RootHelper.canUseRoot()}")
    }

    private fun updateUI() {
        val isDeviceOwner = systemUIManagement.isAppDeviceOwner()
        binding.checkDeviceOwner.isEnabled = isDeviceOwner
        binding.checkDeviceOwner.isChecked = isDeviceOwner

        binding.checkLockSysBar.isEnabled = isDeviceOwner
        binding.checkLockSysBar.isChecked = systemUIManagement.isLockTaskMode()

        binding.checkHideSysBar.isEnabled = SystemUIAccessor.hasPermission(this)
        binding.checkHideSysBar.isChecked = SystemUIAccessor.isHide(this)

        if (isDeviceOwner) {
            systemUIManagement.whitelistAppForLockTask()
        }
    }

    private fun initEvent() {
        binding.checkDeviceOwner.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                if (!systemUIManagement.removeDeviceOwner()) {
                    Toast.makeText(this, "Failed to remove device owner.", Toast.LENGTH_LONG)
                        .show()
                }
            }
            updateUI()
        }

        binding.checkLockSysBar.setOnCheckedChangeListener { _, isChecked ->
            systemUIManagement.setSystemUiDisabled(this, isChecked);
        }

        binding.checkHideSysBar.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                SystemUIAccessor.hideSystemBars(this)
            } else {
                SystemUIAccessor.showSystemBars(this)
            }
        }
    }

    private fun registerReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
            object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val event = KioskDeviceAdminReceiver.getEvent(intent)
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                    when (event) {
                        KioskDeviceAdminReceiver.Companion.DeviceAdminEvent.DEVICE_ADMIN_ENABLED -> {
                            updateUI()
                        }

                        KioskDeviceAdminReceiver.Companion.DeviceAdminEvent.DEVICE_ADMIN_DISABLED -> {
                            updateUI()
                        }

                        KioskDeviceAdminReceiver.Companion.DeviceAdminEvent.LOCK_TASK_MODE_ENTERING -> {
                            binding.checkLockSysBar.isChecked = true
                        }

                        KioskDeviceAdminReceiver.Companion.DeviceAdminEvent.LOCK_TASK_MODE_EXITING -> {
                            binding.checkLockSysBar.isChecked = false
                        }

                        KioskDeviceAdminReceiver.Companion.DeviceAdminEvent.UNSPECIFIED -> {
                            throw UnsupportedOperationException()
                        }
                    }
                }
            }.also {
                localDeviceAdminReceiver = it
            },
            IntentFilter(KioskDeviceAdminReceiver.getAdminAction(this))
        )
    }

    private fun unregisterReceiver() {
        localDeviceAdminReceiver?.let {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver()
    }
}