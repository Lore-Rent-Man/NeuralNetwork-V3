import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.*;
import org.ejml.simple.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.imageio.ImageIO;
 
public class readTestFile {
	
	ArrayList<byte[]> images = new ArrayList<byte[]>();
	ArrayList<SimpleMatrix> inputs = new ArrayList<SimpleMatrix>();
	ArrayList<SimpleMatrix> expected = new ArrayList<SimpleMatrix>();
	
	public void newImage(byte[] arr, int c) {
		File output = new File("C:/Users/s-manl/Documents/pictures2/pic" + c + ".png");
		BufferedImage img = new BufferedImage(28, 28, BufferedImage.TYPE_BYTE_GRAY);
		img.setData(Raster.createRaster(img.getSampleModel(), new DataBufferByte(arr, arr.length), new Point()));
		try {
			ImageIO.write(img, "png", output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public readTestFile() {
        String inputFile = "t10k-images-idx3-ubyte";

		try {
            InputStream inputStream = new FileInputStream(inputFile);
            byte[] headerBytes = new byte[4];
            byte[] picBytes = new byte[784];           
        	for (int i = 0; i < 4; i++) {
            	inputStream.read(headerBytes);
            }
        	
        	for (int image = 0; image < 10000; image++) {
            	inputStream.read(picBytes);
            	images.add(picBytes);
                picBytes = new byte[784];
            }
        	
    		for (int image = 0; image < 10000; image++) {
    			SimpleMatrix z = new SimpleMatrix(784, 1);
    			for (int c = 0; c < 784; c++) {
    				z.set(c, 0, (int)(((double)Byte.toUnsignedInt(images.get(image)[c])/255)+0.5));
    			}
    		    inputs.add(z);
    		}
        } catch (IOException ex) {
            ex.printStackTrace();
        }
		
		inputFile = "t10k-labels-idx1-ubyte";
		
		try {
            InputStream inputStream = new FileInputStream(inputFile);
                   
            byte[] headerBytes = new byte[4];
            byte[] labelBytes = new byte[10000];
            
        	for (int i = 0; i < 2; i++) {
            	inputStream.read(headerBytes);
            }
        	        	
        	inputStream.read(labelBytes);
        	for (byte a:labelBytes) {
            	SimpleMatrix z = new SimpleMatrix(10, 1);
        		z.set(a, 0, 1);
        		expected.add(z);
        	}
        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}
}
