package com.example.huidutest.lib;


import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
public class CommandLine {
    private static final String TAG = "CommandLine";
    private static final long DEFAULT_TIMEOUT_SECONDS = 60; // 默认超时时间60秒

    public static ExecuteResult executeSu(String command) {
        return execute(command, true);
    }

    /**
     * 核心执行方法。
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static ExecuteResult execute(String command, boolean asRoot) {
        Process process = null;
        int exitCode = -1;
        final StringBuilder stdout = new StringBuilder();
        final StringBuilder stderr = new StringBuilder();

        try {
            ProcessBuilder processBuilder;
            if (asRoot) {
                // 只启动 su 进程，不带任何参数
                processBuilder = new ProcessBuilder("su");
            } else {
                processBuilder = new ProcessBuilder(command.split("\\s+"));
            }

            process = processBuilder.start();

            // --- 读取流的逻辑保持不变 ---
            final Process finalProcess = process;
            Thread stdoutReader = new Thread(() -> readStream(finalProcess.getInputStream(), stdout));
            Thread stderrReader = new Thread(() -> readStream(finalProcess.getErrorStream(), stderr));
            stdoutReader.start();
            stderrReader.start();

            if (asRoot) {
                // 对于 root 命令，将命令写入 su 进程的标准输入流
                try (OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream())) {
                    writer.write(command + "\n");
                    writer.flush();
                    // 必须写入 exit 命令来终止 su shell，否则进程会一直挂起
                    writer.write("exit\n");
                    writer.flush();
                }
            }

            // --- 后面的逻辑保持不变 ---
            boolean finished = process.waitFor(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            stdoutReader.join();
            stderrReader.join();

            if (finished) {
                exitCode = process.exitValue();
            } else {
                stderr.append("\nCommand timed out.");
                exitCode = -1;
            }

        } catch (IOException | InterruptedException e) {
            Log.e(TAG, "Exception while executing command: '" + command + "'", e);
            stderr.append("\nException: ").append(e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        return new ExecuteResult(exitCode, stdout.toString().trim(), stderr.toString().trim());
    }

    /**
     * 读取输入流并将其内容追加到 StringBuilder 的辅助方法。
     */
    private static void readStream(InputStream inputStream, StringBuilder builder) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ExecuteResult {
        public String errorMsg;
        public int exitCode;
        public String output;

        public ExecuteResult(int code) {
            this.exitCode = code;
        }

        public ExecuteResult(int code, String output, String errorMsg) {
            this.exitCode = code;
            this.output = output;
            this.errorMsg = errorMsg;
        }

        public String toString() {
            String str = "exitCode: " + this.exitCode + "\n";
            String str2 = this.output;
            if (str2 != null && str2.trim().length() > 0) {
                str = str + "output: " + this.output + "\n";
            }
            String str3 = this.errorMsg;
            if (str3 == null || str3.trim().length() <= 0) {
                return str;
            }
            return str + "error: " + this.errorMsg + "\n";
        }
    }
}