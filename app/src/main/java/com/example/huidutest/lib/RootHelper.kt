package com.example.huidutest.lib

import java.io.DataOutputStream
import java.io.IOException

object RootHelper {
    fun canUseRoot(): Boolean {
        var process: Process? = null
        return try {
            process = Runtime.getRuntime().exec("su")
            // 使用 use 块确保 DataOutputStream 被正确关闭
            DataOutputStream(process.outputStream).use { os ->
                os.writeBytes("id")
                os.flush()
                os.writeBytes("exit")
                os.flush()
            }
            val exitValue = process.waitFor()
            exitValue == 0
        } catch (e: IOException) {
            // 处理 I/O 相关的异常
            false
        } catch (e: InterruptedException) {
            // 恢复中断状态
            Thread.currentThread().interrupt()
            false
        } catch (e: SecurityException) {
            e.printStackTrace()
            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            process?.destroy()
        }
    }
}
