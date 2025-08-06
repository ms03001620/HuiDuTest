package com.example.huidutest.lib

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.huidutest.databinding.ActivityHuiduBinding


class HuiduDeviceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHuiduBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHuiduBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.checkLockSysBar.setOnCheckedChangeListener { _, isChecked ->
            PlatformTool.setStatusBarState(this, if(isChecked) 1 else 0)
            PlatformTool.setNavBarState(this, if(isChecked) 1 else 0)
        }
    }

}