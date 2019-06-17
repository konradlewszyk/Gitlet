package gitlet;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Serialize implements java.io.Serializable {


    public static byte[] toByteArray(Serializable o, String fileName) throws IOException {
        File outFile = new File(fileName);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
        oos.writeObject(o);
        oos.flush();
        out.writeObject(o);
        byte[] byteArray = baos.toByteArray();
        oos.close();
        out.close();
        return byteArray;
    }

    public static Object toObject(String fileName) throws IOException, ClassNotFoundException {
        Object o;
        File inFile = new File(fileName);
        ObjectInputStream inp = new ObjectInputStream(new FileInputStream(inFile));
        o = inp.readObject();
        inp.close();
        return o;
    }

    public static byte[] objToByte(Object tcpPacket) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
        objStream.writeObject(tcpPacket);
        return byteStream.toByteArray();
    }

}



