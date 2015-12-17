package com.example.admin_menu.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class TypeConvertUtils {
	public static final String				TAG											= "TypeConvertUtils";
	
    /**
     * byte[] ¡æ int
     * @param bytes must is 1 ~ 4 bytes.
     */
    public static final int byteArrayToInt(final byte[] bytes) {
        int newValue = 0;
        switch(bytes.length) {
        case 1:
            newValue |= ((int)bytes[0]) & 0xFF;
            break;
        case 2:
            newValue |= (((int)bytes[0]) << 8) & 0xFF00;
            newValue |= ((int)bytes[1]) & 0xFF;
            break;
        case 3:
            newValue |= (((int)bytes[0]) << 16) & 0xFF0000;
            newValue |= (((int)bytes[1]) << 8) & 0xFF00;
            newValue |= ((int)bytes[2]) & 0xFF;
            break;
        case 4:
            newValue |= (((int)bytes[0]) << 24) & 0xFF000000;
            newValue |= (((int)bytes[1]) << 16) & 0xFF0000;
            newValue |= (((int)bytes[2]) << 8) & 0xFF00;
            newValue |= ((int)bytes[3]) & 0xFF;
        }
        return newValue;
    }
    
    /**
     * Convert the int to an byte array.
     * @param integer The integer
     * @return The byte array
     */
    public static final byte[] intToByteArray(final int integer) {
    	 ByteBuffer buff = ByteBuffer.allocate(Integer.SIZE / 8);
    	 buff.putInt(integer);
    	 buff.order(ByteOrder.BIG_ENDIAN);
    	 return buff.array();
    }
    
    /**
     * Convert the short to an byte array.
     * @param short The short
     * @return The byte array
     */
    public static final byte[] shortToByteArray(final short s) {
    	 ByteBuffer buff = ByteBuffer.allocate(Short.SIZE / 8);
    	 buff.putShort(s);
    	 buff.order(ByteOrder.BIG_ENDIAN);
    	 return buff.array();
    }
    
    public static final byte[] getbytes(byte src[], int offset, int length)
    {
        byte dest[] = new byte[length];
        System.arraycopy(src, offset, dest, 0, length);
        return dest;
    }
    
    // Returns the contents of the file in a byte array.
    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
    
        // Get the size of the file
        long length = file.length();
    
        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
    
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
    
        // Close the input stream and return bytes
        is.close();
        return bytes;
    }
    
    // Returns an output stream for a ByteBuffer.
    // The write() methods use the relative ByteBuffer put() methods.
    public static OutputStream ByteBufferToOutputStream(final ByteBuffer buf) {
    	return new OutputStream() {
    		public synchronized void write(int b) throws IOException {
    			buf.put((byte)b);
    		}

    		public synchronized void write(byte[] bytes, int off, int len) throws IOException {
    			buf.put(bytes, off, len);
    		}
    	};
    }
    
	// Returns an input stream for a ByteBuffer.
	// The read() methods use the relative ByteBuffer get() methods.
	public static InputStream ByteBufferToInputStream(final ByteBuffer buf) {
		return new InputStream() {
			public synchronized int read() throws IOException {
				if (!buf.hasRemaining()) {
					return -1;
				}
				return buf.get();
			}

			public synchronized int read(byte[] bytes, int off, int len) throws IOException {
				// Read only what's left
				len = Math.min(len, buf.remaining());
				buf.get(bytes, off, len);
				return len;
			}
		};
	}
	
	public static Bitmap getBitmap(Drawable drawable)
	{
		 Bitmap bitmap = Bitmap.createBitmap(drawable.getMinimumWidth(), drawable.getMinimumHeight(), Bitmap.Config.ARGB_8888);
		 Canvas canvas = new Canvas(bitmap);
		 drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		 drawable.draw(canvas);

		 return bitmap;
	}
}
