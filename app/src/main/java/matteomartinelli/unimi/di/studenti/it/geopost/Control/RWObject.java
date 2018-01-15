package matteomartinelli.unimi.di.studenti.it.geopost.Control;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by teo on 13/01/18.
 */

public class RWObject {
    public final static String USER_BUNDLE = "userBundle";

    public static Object readObject(Context context, String fileName) {
        FileInputStream inputStream = null;
        ObjectInputStream objReader = null;
        Object readedObject = null;
        try {
            inputStream = context.openFileInput(fileName);
            ObjectInputStream objectInputStream = objReader = new ObjectInputStream(inputStream);
            readedObject = objReader.readObject();
            objectInputStream.close();
            inputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return readedObject;
    }

    public static void writeObject(Context context, String fileName, Object toWrite) {
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectWriter = null;
        try {
            fileOutputStream = context.openFileOutput(fileName, context.MODE_PRIVATE);
            objectWriter = new ObjectOutputStream(fileOutputStream);
            objectWriter.writeObject(toWrite);
            objectWriter.close();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}