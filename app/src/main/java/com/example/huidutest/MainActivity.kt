package com.example.huidutest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.huidutest.databinding.ActivityMainBinding
import com.example.huidutest.lib.DeviceOwnerChecker
import com.example.huidutest.lib.KioskDeviceAdminReceiver
import com.example.huidutest.lib.SystemUIAccessor
import com.example.huidutest.lib.SystemUIManagement


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var systemUIManagement: SystemUIManagement? = null
    private var localDeviceAdminReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        systemUIManagement = SystemUIManagement(this, KioskDeviceAdminReceiver::class.java)

        initEvent()
        updateUI()
        registerReceiver()
    }

    private fun updateUI() {
        val isDeviceOwner = DeviceOwnerChecker.isAppDeviceOwner(this)
        binding.checkDeviceOwner.isEnabled = isDeviceOwner
        binding.checkDeviceOwner.isChecked = isDeviceOwner

        binding.checkLockSysBar.isEnabled = isDeviceOwner
        binding.checkLockSysBar.isChecked = false//systemUIManagement.isLockSystemBar()

        binding.checkHideSysBar.isEnabled = isDeviceOwner
        if (isDeviceOwner) {
            systemUIManagement?.whitelistAppForLockTask()
        }
    }

    private fun initEvent() {
        binding.checkDeviceOwner.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                if (!DeviceOwnerChecker.removeDeviceOwner(this)) {
                    Toast.makeText(this, "Failed to remove device owner.", Toast.LENGTH_LONG)
                        .show()
                }
            }
            updateUI()
        }

        binding.checkHideSysBar.setOnCheckedChangeListener { _, isChecked ->
            systemUIManagement?.setSystemUiDisabled(this, isChecked);

            if (SystemUIAccessor.hasPermission(this)) {
                if (isChecked) {
                    SystemUIAccessor.hideSystemBars(this)
                } else {
                    SystemUIAccessor.showSystemBars(this)
                }
            } else {
                Toast.makeText(
                    this,
                    "WRITE_SECURE_SETTINGS Permission not granted.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun registerReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
            object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val event = KioskDeviceAdminReceiver.getEvent(intent)
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                    if (event == KioskDeviceAdminReceiver.Companion.DeviceAdminEvent.DEVICE_ADMIN_DISABLED ||
                        event == KioskDeviceAdminReceiver.Companion.DeviceAdminEvent.DEVICE_ADMIN_ENABLED
                    ) {
                        updateUI()
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