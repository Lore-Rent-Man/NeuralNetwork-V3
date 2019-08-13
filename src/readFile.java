import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.*;
import org.ejml.simple.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.imageio.ImageIO;
 
public class readFile {
	
	ArrayList<byte[]> images = new ArrayList<byte[]>();
	
	public readFile() {
        String inputFile = "train-images.idx3-ubyte";

		try {
        	
            InputStream inputStream = new FileInputStream(inputFile);
                    	 
            byte[] headerBytes = new byte[4];
            byte[] picBytes = new byte[784];
                        
        	for (int i = 0; i < 4; i++) {
            	inputStream.read(headerBytes);
            }
        	        	
        	for (int image = 0; image < 60000; image++) {
            	inputStream.read(picBytes);
            	images.add(picBytes);
                picBytes = new byte[784];
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}
	
	public ArrayList<ArrayList<SimpleMatrix>> getEpochLabels(int mini_size, int epoch_size, int epoch_num){
		ArrayList<ArrayList<SimpleMatrix>> epoch = new ArrayList<ArrayList<SimpleMatrix>>();
		ArrayList<SimpleMatrix> mini_batch  = new ArrayList<SimpleMatrix>();
        String inputFile = "train-labels-idx1-ubyte";
        try {
            InputStream inputStream = new FileInputStream(inputFile);
        	
            long fileSize = new File(inputFile).length();
            
            byte[] headerBytes = new byte[4];
            byte[] spacerBytes = new byte[(epoch_num-1) * epoch_size];
            byte[] labelBytes = new byte[epoch_size];
            
        	for (int i = 0; i < 2; i++) {
            	inputStream.read(headerBytes);
            }
        	
        	inputStream.read(spacerBytes);
        	
        	inputStream.read(labelBytes);
        	int counter = 1;
        	for (byte a:labelBytes) {
            	SimpleMatrix z = new SimpleMatrix(10, 1);
        		z.set(a, 0, 1);
        		mini_batch.add(z);
        		if (counter % mini_size == 0) {
        			epoch.add(mini_batch);
        			mini_batch = new ArrayList<SimpleMatrix>();
        		}
        		counter++;
        	}
        	return epoch;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
	}
	
	public ArrayList<ArrayList<SimpleMatrix>> getImages(int mini_size, int epoch_size, int epochnum){
		ArrayList<ArrayList<SimpleMatrix>> inputs  = new ArrayList<ArrayList<SimpleMatrix>>();
		ArrayList<SimpleMatrix> mini_batch  = new ArrayList<SimpleMatrix>();
        int counter = 1;
		for (int image = (epochnum - 1) * epoch_size; image < epochnum * epoch_size; image++) {
			SimpleMatrix z = new SimpleMatrix(784, 1);
			for (int c = 0; c < 784; c++) {
				z.set(c, 0, (int)(((double)Byte.toUnsignedInt(images.get(image)[c])/255)+0.5));
			}
		    mini_batch.add(z);
		    if (counter % (mini_size) == 0) {
		    	inputs.add(mini_batch);
		    	mini_batch = new ArrayList<SimpleMatrix>();
		    }
		    counter++;
		}
		return inputs;
	}
	
}
