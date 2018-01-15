package com.piaofirst.lightdemo

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class MainActivity : AppCompatActivity() {
    private lateinit var  manager: CameraManager// 声明CameraManager对象
    private var m_Camera: Camera? = null// 声明Camera对象

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val camerList = manager.cameraIdList
            for (str in camerList) {
            }
        } catch (e: CameraAccessException) {
            Log.e("error", e.message)
        }

        btn_open.onClick {  lightSwitch(false)  }
        btn_close.onClick { lightSwitch(true) }
    }

    /**
     * 手电筒控制方法
     *
     * @param lightStatus
     * @return
     */
    private fun lightSwitch(lightStatus: Boolean) {
        if (lightStatus) { // 关闭手电筒
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    manager.setTorchMode("0", false)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else {
                if (m_Camera != null) {
                    m_Camera!!.stopPreview()
                    m_Camera!!.release()
                    m_Camera = null
                }
            }
        } else { // 打开手电筒
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    manager.setTorchMode("0", true)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else {
                val pm = packageManager
                val features = pm.systemAvailableFeatures
                for (f in features) {
                    if (PackageManager.FEATURE_CAMERA_FLASH == f.name) { // 判断设备是否支持闪光灯
                        if (null == m_Camera) {
                            m_Camera = Camera.open()
                        }
                        val parameters = m_Camera!!.getParameters()
                        parameters.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                        m_Camera!!.setParameters(parameters)
                        m_Camera!!.startPreview()
                    }
                }
            }
        }
    }

    /**
     * 判断Android系统版本是否 >= M(API23)
     */
    private fun isM(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }
}
