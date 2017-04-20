package com.auto.auto;

import android.app.Application;
import android.os.Environment;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.flattener.Flattener;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.newland.support.nllogger.LogUtils;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

public class FightApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LogConfiguration config = new LogConfiguration.Builder()
                .build();
        Printer[] printers = new Printer[2];
        printers[0] = new AndroidPrinter();
        String filePath = new File(Environment.getExternalStorageDirectory(),"loggers").getPath();

        final DateFormat format = DateFormat.getDateTimeInstance();

        FilePrinter filePrinter = new FilePrinter.Builder(filePath)
                .logFlattener(new Flattener() {
                    @Override
                    public CharSequence flatten(int logLevel, String tag, String message) {
                        return format.format(new Date())
                                + '|' + LogLevel.getShortLevelName(logLevel)
                                + '|' + tag
                                + '|' + message;
                    }
                })
                .build();
        printers[1] = filePrinter;
        LogUtils.init(config,printers);
        LogUtils.setDebug(BuildConfig.DEBUG);
    }
}
