import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;



/**
 * @author ShroukRashwan
 *
 */

class ImageController extends Canvas {
	
    Image original;
    Shape selectedArea;
    BufferedImage bufferedImage;
    BufferedImage edited;
    BufferedImage cropedPart;
    BufferedImage cropedEdited;
    Dimension screenDimension; 
    MediaTracker trackImageLoading; 
    boolean isReadyToSave;
    boolean isImageLoaded;
    boolean isInverted;
    boolean isBlured;
    boolean isChanged;
    boolean isRectangularCrop;
    boolean isCircularCrop;
    String imagePath;
    float angle, brightnessLevel;
    int centerX, centerY;	
    Point startDrag, endDrag;
    int partStartX, partStartY , partW, partH;

    //dashed line
    final static float dash1[] = { 10.0f };
    final static BasicStroke dashed = new BasicStroke(3.0f,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);

    public ImageController(EditImage frame)
    {
       screenDimension=getToolkit().getScreenSize(); //get the screen size   
       centerX=(int)screenDimension.getWidth()/2; //half of the screen width
       centerY=(int)screenDimension.getHeight()/2;//half of the screen height

       final EditImage mainFrame = frame;

       /* Selecting Area Code */
       this.addMouseListener(new MouseListener() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                            // TODO Auto-generated method stub

                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                            // TODO Auto-generated method stub
                            startDrag = new Point(e.getX(), e.getY());
                            endDrag = startDrag;
                            repaint();

                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                            // TODO Auto-generated method stub
                        if(endDrag!=null && startDrag!=null) {
                            try 
                            {
//                            	int x=centerX-bufferedImage.getWidth()/2;
//                                int y=centerY-bufferedImage.getHeight()/2;
                                selectedArea = makeRectangle(startDrag.x, startDrag.y, e.getX(), e.getY());
                                //mainFrame.updateSelectedRegion(bufferedImage.getSubimage(startDrag.x, startDrag.y, e.getX()-startDrag.x -x , e.getY()-startDrag.y -y));   
                                startDrag = null;
                                endDrag = null;
                                repaint();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }   
                        }

                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                            // TODO Auto-generated method stub

                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                            // TODO Auto-generated method stub

                    }

            });


       this.addMouseMotionListener(new MouseMotionAdapter() {
       public void mouseDragged(MouseEvent e) {
           endDrag = new Point(e.getX(), e.getY());
           repaint();
       }   
   });
    }

    public boolean isRectangularCrop() {
            return isRectangularCrop;
    }

    public void setRectangularCrop(boolean isCircularCrop) {
            this.isRectangularCrop = isCircularCrop;
    }

    public boolean isCircularCrop() {
            return isCircularCrop;
    }

    public void setCircularCrop(boolean isCircularCrop) {
            this.isCircularCrop = isCircularCrop;
    }

    public boolean isImageLoaded() {
            return isImageLoaded;
    }

    public void setImageLoaded(boolean isImageLoaded) {
            this.isImageLoaded = isImageLoaded;
    }

    public boolean isInverted() {
            return isInverted;
    }

    public void setInverted(boolean isInverted) {
            this.isInverted = isInverted;
    }

    public boolean isBlured() {
            return isBlured;
    }

    public void setBlured(boolean isBlured) {
            this.isBlured = isBlured;
    }

    public boolean isChanged() {
            return isChanged;
    }

    public void setChanged(boolean isChanged) {
            this.isChanged = isChanged;
    }

    public String getImagePath() {
            return imagePath;
    }

    public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
    }

    public float getBrightnessLevel() {
            return brightnessLevel;
    }

    public void setBrightnessLevel(float brightnessLevel) {
            this.brightnessLevel = brightnessLevel;
    }
    
    public boolean isIsReadyToSave() {
        return isReadyToSave;
    }

    public void setIsReadyToSave(boolean isReadyToSave) {
        this.isReadyToSave = isReadyToSave;
    }

    public void initialize()
    {
        isImageLoaded=false; 
        isInverted=false;
        isBlured=false;
        isChanged=false;
        isCircularCrop= false;
        isRectangularCrop = false;
        isReadyToSave = false;
        angle=0.0f;
        brightnessLevel=0.0f;
    }

    private Rectangle2D.Float makeRectangle(int x1, int y1, int x2, int y2) {

           return new Rectangle2D.Float(Math.min(x1, x2), Math.min(y1, y2),
                   Math.abs(x1 - x2), Math.abs(y1 - y2));
       }

    @Override
    public void paint(Graphics g)
    {
        Graphics2D g2d=(Graphics2D)g; //create Graphics2D object  		
        if(isImageLoaded)
        {
            int x=centerX-bufferedImage.getWidth()/2;
            int y=centerY-bufferedImage.getHeight()/2;

            if (isRectangularCrop)
            {
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);		            
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));

                if (selectedArea != null) 
                {
                    g2d.setPaint(Color.MAGENTA);
                    g2d.setStroke(dashed);
                    g2d.draw(selectedArea);
                    partStartX = selectedArea.getBounds().x-x; 
                    partStartY = selectedArea.getBounds().y-y; 
                    partW = selectedArea.getBounds().width;
                    partH = selectedArea.getBounds().height;
                    cropedPart = bufferedImage.getSubimage(selectedArea.getBounds().x-x,selectedArea.getBounds().y-y,selectedArea.getBounds().width,selectedArea.getBounds().height);
                    cropedEdited = cropedPart;
                    isRectangularCrop = false;
                }

                if (startDrag != null && endDrag != null && selectedArea == null) 
                {
                g2d.setPaint(Color.MAGENTA);
                Shape r = makeRectangle(startDrag.x, startDrag.y, endDrag.x,
                        endDrag.y);
                g2d.draw(r);
                }

                g2d.translate(x,y); //move to  coordinate (x,y)
                g2d.drawImage(bufferedImage,0,0,null); //draw image
            }
            else if(cropedPart != null)
            {
                isRectangularCrop = false;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);		            
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.99f));
                g2d.setPaint(Color.MAGENTA);
                g2d.setStroke(dashed);
                g2d.draw(selectedArea);
                g2d.translate(x,y);   
                if (isChanged || isBlured || isInverted)
                {
              
                   edited = combineImages(bufferedImage, cropedEdited);
                  // g2d.drawImage(cropedEdited,0,0,null); 
                   g2d.drawImage(edited,0,0,null); 
                
                }
                else
                {
                    g2d.drawImage(bufferedImage,0,0,null); 
                }
            }
            else
            {
                g2d.translate(x,y); //move to  coordinate (x,y)
                g2d.drawImage(bufferedImage,0,0,null); //draw image
            }
        }
        g2d.dispose(); //clean the Graphic2D object
    }

    public void prepareImage(String imagePath)
    {
       initialize();
       try{
               //track the image loading
               trackImageLoading = new MediaTracker(this);    
               original = Toolkit.getDefaultToolkit().getImage(imagePath); 
               trackImageLoading.addImage(original,0);
               trackImageLoading.waitForID(0); 
               //get the image width and height  
               int width = original.getWidth(null);
               int height = original.getHeight(null);
               //create buffered image from the image so any change to the image can be made
               bufferedImage = createBufferedImageFromImage(original,width,height,false);
               //create the blank buffered image
               //the update image data is stored in the buffered image   
               edited = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);  
               isImageLoaded = true; //now the image is loaded
       }
       catch(Exception e){
               System.exit(-1);
               }
    }

    public BufferedImage createBufferedImageFromImage(Image image, int width, int height, boolean tran)
    { 
            BufferedImage dest ;
            if(tran) 
                 dest = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            else
             dest = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            Graphics2D g2 = dest.createGraphics();
            g2.drawImage(image, 0, 0, null);
            g2.dispose();
            return dest;
    }

    public void invertImage()
    {
        BufferedImage toInvert;
        if (isChanged || isBlured )
        {
            toInvert = cropedEdited;
        }
        else
        {
             toInvert = cropedPart;
        }
        for (int x = 0; x < toInvert.getWidth(); x++) {
            for (int y = 0; y < toInvert.getHeight(); y++) {
                int rgba = toInvert.getRGB(x, y);
                Color col = new Color(rgba, true);
                col = new Color(255 - col.getRed(),
                                255 - col.getGreen(),
                                255 - col.getBlue());
                toInvert.setRGB(x, y, col.getRGB());
            }
        }
        repaint();
    }
    
    public BufferedImage combineImages(BufferedImage original, BufferedImage part)
    {

        
        for (int i = partStartX; i < partW + partStartX  ; i++)
        {
            for (int j= partStartY; j< partH  + partStartY ; j++)
            {

                original.setRGB(i, j, part.getRGB(i-partStartX, j-partStartY));
            }
        }
        return original;
    }
    
   public void filterImage()
   {
	   if (!isInverted || !isBlured )
       {
            cropedEdited = cropedPart;
       }
	    float[] elements = {0.0f, 1.0f, 0.0f, -1.0f,brightnessLevel,1.0f,0.0f,0.0f,0.0f}; 
	    Kernel kernel = new Kernel(3, 3, elements);  
	    BufferedImageOp  change  = new ConvolveOp(kernel); 
	    cropedEdited = change.filter(cropedEdited, null);
       repaint();
   }
   
   public void blurImage()
   {
        if (!isChanged || !isBlured )
        {
             cropedEdited = cropedPart;
        }
	    float[] elements = {1/9f, 1/9f, 1/9f, 1/9f,1/9f,1/9f,1/9f,1/9f,1/9f,1/9f}; 
	    Kernel kernel = new Kernel(3, 3, elements);  
	    BufferedImageOp  blur  = new ConvolveOp(kernel); 
	    cropedEdited = blur.filter(cropedEdited, null);
        repaint();
   }
   
   public void saveToFile(String filename)
   {
    String ftype=filename.substring(filename.lastIndexOf('.')+1);
    try
    {
     if(isReadyToSave)
      ImageIO.write(cropedEdited,ftype,new File(filename));
    }
    catch(IOException e)
    {
        System.out.println("Error in saving the file");
    }
  }

}


public class EditImage extends JFrame implements ActionListener{

	JFileChooser filechooser;
	// Toolbar & its icons
	JMenuBar toolbar;
	JMenu edit;
	JMenu crop;
	JMenu imageFile;
	JMenu action;
	
	// Edit Menu Items
	JMenuItem brightness;
	JMenuItem invert;
	JMenuItem blur;
	
	// Image Menu Items
	JMenuItem uploadImage;
	JMenuItem saveImage;
	JMenuItem saveAsImage;
	
	// Crop Menu Items
	JMenuItem circular;
	JMenuItem rectangular;
	
	// Crop Menu Items
	JMenuItem undo;
	JMenuItem redo;
	
	private JPanel selectedAreaPanel;
	
	String imagePath;
	
	// create instance of Image Controller
	ImageController controller;
	// create container to add image into window
	Container cont = new Container();
	
	public EditImage()
	{
		controller = new ImageController(this);
		
		// create container to add image into window
		Container imageContainer = getContentPane();
		imageContainer.add(controller, BorderLayout.CENTER);
		
		// toolbar
		toolbar = new JMenuBar();
		
		//Init imageFile Menu
		imageFile = new JMenu("Image File");
		uploadImage = new JMenuItem("Upload Image");
		uploadImage.addActionListener(this);
		saveImage = new JMenuItem("Save Image");
		saveImage.addActionListener(this);
		saveAsImage = new JMenuItem("Save As Image");
		saveAsImage.addActionListener(this);
		
		imageFile.add(uploadImage);
		imageFile.add(saveImage);
		imageFile.add(saveAsImage);
		
		//Init Edit Menu
		edit = new JMenu("Edit Image");
		blur = new JMenuItem("Blur Image");
		blur.addActionListener(this);
		invert = new JMenuItem("Invert Image");
		invert.addActionListener(this);
		brightness = new JMenuItem("Change Image Light Level");
		brightness.addActionListener(this);
		
		edit.add(blur);
		edit.add(invert);
		edit.add(brightness);
		
		//Init Crop Menu
		crop = new JMenu ("Crop Image");
		circular = new JMenuItem("Select Circular Area");
		circular.addActionListener(this);
		rectangular = new JMenuItem("Select Rectangular Area");
		rectangular.addActionListener(this);
		
		crop.add(circular);
		crop.add(rectangular);
		
		//Init action Menu
		action = new JMenu ("Action");
		undo = new JMenuItem("Undo Last Action");
		undo.addActionListener(this);
		redo = new JMenuItem("Redo Last Action");
		redo.addActionListener(this);
		
		action.add(undo);
		action.add(redo);
		
		// add Menus to toolbar
		toolbar.add(imageFile);
		toolbar.add(crop);
		toolbar.add(edit);
		toolbar.add(action);
		
		// set toolbar
		setJMenuBar(toolbar);
		
		// set window properties 
		setTitle("Image Editor");
		setExtendedState(this.getExtendedState() | this.MAXIMIZED_BOTH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		
		//prepare for uploading image
		filechooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "gif","bmp","png");
		filechooser.setFileFilter(filter);
		filechooser.setMultiSelectionEnabled(false);
		//enableSaving(false);
		controller.requestFocus();
		
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
			new EditImage();
	}
	
	public void getImagePath() {  
		
		int returnVal = filechooser.showOpenDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION) 		
		{   
			imagePath=filechooser.getSelectedFile().toString();
			controller.prepareImage(imagePath);
		}        
	}
	
	public void getImage()
	{  
		int returnVal = filechooser.showOpenDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION) 		
		{   
			imagePath=filechooser.getSelectedFile().toString();
			controller.prepareImage(imagePath);
		}        
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		//get action source
		JMenuItem actionSource = (JMenuItem) e.getSource();
		
		if (0 == actionSource.getText().compareTo("Upload Image"))
		{
                    getImage();
		    controller.repaint();
		    validate();			
		}
		else if (0 == actionSource.getText().compareTo("Save As Image"))
		{
                    showSaveFileDialog();
		}
		else if (0 == actionSource.getText().compareTo("Save Image"))
		{
			 controller.saveToFile(imagePath);
		}
		else if (0 == actionSource.getText().compareTo("Blur Image"))
		{
            if(controller.isImageLoaded())
            {
                controller.setIsReadyToSave(true);
                controller.setChanged(true);
                controller.blurImage();
            } 
		}
		else if (0 == actionSource.getText().compareTo("Invert Image"))
		{
            if(controller.isImageLoaded())
            {
                controller.setIsReadyToSave(true);
                controller.setInverted(true);
                controller.invertImage();
            } 
		}
		else if (0 == actionSource.getText().compareTo("Change Image Light Level"))
		{
            ImageBrightness ib=new ImageBrightness(); 
		    if(controller.isImageLoaded()) 
            {
		    controller.setIsReadyToSave(true);
		     ib.enableSlider(true); 
		    }
		}
		else if (0 == actionSource.getText().compareTo("Select Circular Area"))
		{
			
		}
		else if (0 == actionSource.getText().compareTo("Select Rectangular Area"))
		{
			/* put in generic place and change the drawn shape according to option */
			controller.setRectangularCrop(true);
		}
		else if (0 == actionSource.getText().compareTo("Undo Last Action"))
		{
			
		}
		else if (0 == actionSource.getText().compareTo("Redo Last Action"))
		{
			
		}
	}
	
	public void updateSelectedRegion(BufferedImage bufferedImage) {
        Graphics g = selectedAreaPanel.getGraphics();
        g.clearRect(0, 0, 221, 289);
        g.drawImage(bufferedImage, 0, 0, null);
    }
	
    public void showSaveFileDialog()
    {
        int returnVal = filechooser.showSaveDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION)
        {  
            String filen=filechooser.getSelectedFile().toString(); 
             controller.saveToFile(filen);  
            
        }
   }
	
	
public class ImageBrightness extends JFrame implements ChangeListener
{
	JSlider slider;
	 
	ImageBrightness()
	{
		addWindowListener(new WindowAdapter()
		{
            @Override
			public void windowClosing(WindowEvent e)
			{
			     dispose();
			      
			}
		});
		Container cont=getContentPane();  
		slider=new JSlider(-10,10,0); 
		slider.setEnabled(false);
		slider.addChangeListener(this);
		cont.add(slider,BorderLayout.CENTER); 
		slider.setEnabled(true);
		setTitle("Image brightness");
		setPreferredSize(new Dimension(300,100));
		setVisible(true);
		pack();
		enableSlider(false);
	}
	
	public void enableSlider(boolean enabled)
	{
	  slider.setEnabled(enabled);
	}
	
        @Override
	public void stateChanged(ChangeEvent e)
	{
	   controller.setBrightnessLevel(slider.getValue()/10.0f);
	   controller.setChanged(true);   
	   controller.filterImage();
	   controller.repaint();
	   //enableSaving(true);
	   
	}
}


	
}
