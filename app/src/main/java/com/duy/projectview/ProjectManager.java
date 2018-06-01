package com.duy.projectview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.ide.common.xml.AndroidManifestParser;
import com.android.ide.common.xml.ManifestData;
import com.duy.android.compiler.project.AndroidApplicationProject;
import com.duy.android.compiler.project.ClassFile;
import com.duy.android.compiler.project.JavaProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Duy on 16-Jul-17.
 */

public class ProjectManager {

    private static final String CURRENT_PROJECT = "file_project.nide";
    private static final String TAG = "ProjectManager";

    private static final String ANDROID_PROJECT = "ANDROID_PROJECT";
    private static final String ROOT_DIR = "ROOT_DIR";
    private static final String MAIN_CLASS_NAME = "MAIN_CLASS_NAME";
    private static final String PACKAGE_NAME = "PACKAGE_NAME";
    private static final String PROJECT_NAME = "PROJECT_NAME";


    public static void saveProject(@NonNull Context context, @NonNull JavaProject folder) {
//        SharedPreferences preferences = context.getSharedPreferences(CURRENT_PROJECT, Context.MODE_PRIVATE);
//        SharedPreferences.Editor edit = preferences.edit();
//        edit.putBoolean(ANDROID_PROJECT, folder instanceof AndroidApplicationProject);
//        edit.putString(ROOT_DIR, folder.getRootDir().getPath());
//        edit.putString(MAIN_CLASS_NAME, folder.getMainClass().getName());
//        edit.putString(PACKAGE_NAME, folder.getPackageName());
//        edit.putString(PROJECT_NAME, folder.getProjectName());
//        edit.apply();
        try {
            File tmp = new File(context.getFilesDir(), "tmp.proj");
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(tmp));
            outputStream.writeObject(folder);
            outputStream.close();
        } catch (Exception e) {
        }
    }

    @Nullable
    public static JavaProject getLastProject(@NonNull Context context) {
//        SharedPreferences preferences = context.getSharedPreferences(CURRENT_PROJECT, Context.MODE_PRIVATE);
//        boolean androidProject = preferences.getBoolean(ANDROID_PROJECT, false);
//        String rootDir = preferences.getString(ROOT_DIR, null);
//        if (rootDir == null || !(new File(rootDir).exists())) return null;
//        String mainClassName = preferences.getString(MAIN_CLASS_NAME, null);
//        String packageName = preferences.getString(PACKAGE_NAME, null);
//        if (androidProject) {
//            return new AndroidApplicationProject(new File(rootDir), mainClassName, packageName);
//        } else {
//            return new JavaProject(new File(rootDir), mainClassName, packageName);
//        }

        try {
            File tmp = new File(context.getFilesDir(), "tmp.proj");
            ObjectInputStream outputStream = new ObjectInputStream(new FileInputStream(tmp));
            Object o = outputStream.readObject();
            return (JavaProject) o;
        } catch (Exception e) {
        }
        return null;
    }

    @Nullable
    public static JavaProject createProjectIfNeed(Context context, File file) {
        if (file.isFile() || !file.canWrite() || !file.canRead()) {
            return null;
        }
        // TODO: 05-Aug-17 dynamic change classpath
        JavaProject projectFile = new JavaProject(file.getParentFile(), null, null);
        try {
            projectFile.createMainClass();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return projectFile;
    }

    public static AndroidApplicationProject importAndroidProject(Context context, File file) {
        Log.d(TAG, "importAndroidProject() called with: context = [" + context + "], file = [" + file + "]");

        AndroidApplicationProject project = new AndroidApplicationProject(file.getParentFile(),
                null, null);
        try {
            if (project.getXmlManifest().exists()) {
                ManifestData manifestData = AndroidManifestParser.parse(new FileInputStream(project.getXmlManifest()));
                ManifestData.Activity launcherActivity = manifestData.getLauncherActivity();
                if (launcherActivity != null) {
                    project.setMainClass(new ClassFile(launcherActivity.getName()));
                    project.setPackageName(manifestData.getPackage());
                    project.createClassR();
                }
                Log.d(TAG, "importAndroidProject launcherActivity = " + launcherActivity);
            } else {
                return null;
            }
            return project;
        } catch (Exception e) {

        }
        return null;
    }
}