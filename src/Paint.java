import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Paint{
	
	byte[] arr;
	
	public Paint(byte[] arr) {
		this.arr = arr;
	}
	
	public void fileImage(byte[] arr) {
		try {
			BufferedImage img = new BufferedImage(28, 28, BufferedImage.TYPE_BYTE_GRAY);
			img.setData(Raster.createRaster(img.getSampleModel(), new DataBufferByte(arr, arr.length), new Point()));
			File outputfile = new File("pic.png");
			ImageIO.write(img, "png", outputfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
