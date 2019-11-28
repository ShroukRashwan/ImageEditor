import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;




/**
 * @author ShroukRashwan
 *
 */

class ImageController extends Canvas {
	
    /**
	 * Default Version ID
	 */
	private static final long serialVersionUID = 1L;
	
	// image uploaded
	Image original;
	String imagePath;
	
	// selected part and it's dimensions
    Shape selectedArea;
    Point startDrag, endDrag;
    int partStartX, partStartY , partW, partH;
    
    // Buffered image original and edited for whole and selected area
    BufferedImage bufferedImage;
    BufferedImage edited;
    BufferedImage cropedPart;
    BufferedImage cropedEdited;
    
    // Screen variables
    Dimension screenDimension; 
    MediaTracker trackImageLoading; 
    int centerX, centerY;	
    
    // Booleans to track actions
    boolean isReadyToSave;
    boolean isImageLoaded;
    boolean isInverted;
    boolean isBlured;
    boolean isChanged;
    boolean isRectangularCrop;
    boolean isCircularCrop;
    boolean didUndo;    
    float brightnessLevel;
    
    // Undo and Redo arrays
    BufferedImage [] imageCopy ;
    boolean [] statesCopy;

    //dashed line
    final static float dash1[] = { 10.0f };
    final static BasicStroke dashed = new BasicStroke(4.0f,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);

    /**
     * Public constructor
     */
    /**
     * 
     */
    /**
     * 
     */
    /**
     * 
     */
    public ImageController()
    {
       screenDimension=getToolkit().getScreenSize(); //get the screen size   
       centerX=(int)screenDimension.getWidth()/2; //half of the screen width
       centerY=(int)screenDimension.getHeight()/2;//half of the screen height
       
       imageCopy = new BufferedImage[4];
       statesCopy = new boolean[7];

       /* Selecting Area Code for mouse actions */
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

                                selectedArea = makeRectangle(startDrag.x, startDrag.y, e.getX(), e.getY());   
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


       this.addMouseMotionListener(new MouseMotionAdapter() 
       {
	       public void mouseDragged(MouseEvent e) 
	       {
	           endDrag = new Point(e.getX(), e.getY());
	           repaint();
	       }   
       });
    }

    /* Setters and getters for needed variables */
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

    public boolean isDidUndo() {
        return didUndo;
    }

    public void setDidUndo(boolean didUndo) {
        this.didUndo = didUndo;
    }
    

    /**
     * Function to init all variables related to actions 
     */
    public void initialize()
    {
        isImageLoaded=false; 
        isInverted=false;
        isBlured=false;
        isChanged=false;
        isCircularCrop= false;
        isRectangularCrop = false;
        isReadyToSave = false;
        didUndo = false;
        brightnessLevel=0.0f;
    }

    /**
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return Rectangle with given dimensions
     */
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
                   //g2d.drawImage(cropedEdited,0,0,null); 
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

    /**
     * Function to upload the image 
     * @param imagePath path that contain the image
     */
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
               edited = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);  
               isImageLoaded = true; //now the image is loaded
       }
       catch(Exception e)
       {
               System.exit(-1);
        }
    }

    /**
     * Function to convert the loaded image to buffered
     * @param image : the image
     * @param width : it's width
     * @param height : its height
     * @param tran   if transparent or not
     * @return 
     */
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

    /**
     * Function to implement invert image action
     */
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
    
    /**
     * @param original
     * @param part
     * @return the combination of the two images
     */
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
    
    /**
	 * Function to implement change image brightness action
	 */
    public void filterImage()
    {
	   if (!isInverted && !isBlured )
       {
            cropedEdited = cropedPart;
       }
	    float[] elements = {0.0f, 1.0f, 0.0f, -1.0f,brightnessLevel,1.0f,0.0f,0.0f,0.0f}; 
	    Kernel kernel = new Kernel(3, 3, elements);  
	    BufferedImageOp  change  = new ConvolveOp(kernel); 
	    cropedEdited = change.filter(cropedEdited, null);
       repaint();
    }
   
   /**
    * Function to implement blur image  action
 	*/
    public void blurImage()
    {
        if (!isChanged && !isBlured )
        {
             cropedEdited = cropedPart;
        }
	    float[] elements = {1/9f, 1/9f, 1/9f, 1/9f,1/9f,1/9f,1/9f,1/9f,1/9f,1/9f}; 
	    Kernel kernel = new Kernel(3, 3, elements);  
	    BufferedImageOp  blur  = new ConvolveOp(kernel); 
	    cropedEdited = blur.filter(cropedEdited, null);
        repaint();
   }
   
   /**
    * Function for Save as Action
    * @param filename where to save image
 	*/
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
    
   
    /**
	 *  for undo and redo
	 */
	public void saveActions()
    {
	   imageCopy[0] = bufferedImage;
	   imageCopy[1] = edited;
	   imageCopy[2] = cropedPart;
	   imageCopy[3] = bufferedImage;
	   
	   statesCopy[0] = isBlured;
	   statesCopy[1] = isReadyToSave;
	   statesCopy[2] = isChanged;
	   statesCopy[3] = isInverted;
	   statesCopy[4] = isRectangularCrop;
	   statesCopy[5] = isCircularCrop;
	   statesCopy[6] = isImageLoaded;
	   
    }
   
	/**
	 *  for undo and redo
	 */
    public void changeAction()
    {
        bufferedImage = imageCopy[0];
        edited = imageCopy[1];
        cropedPart = imageCopy[2];
        bufferedImage = imageCopy[3];

        isBlured = statesCopy[0];
        isReadyToSave = statesCopy[1];
        isChanged = statesCopy[2];
        isInverted = statesCopy[3];
        isRectangularCrop = statesCopy[4];
        isCircularCrop = statesCopy[5];
        isImageLoaded = statesCopy[6];
    }
    
    /**
	 *  for undo and redo
	 */
    public void UndoRedoAction()
    {
       BufferedImage[] tempImages = new BufferedImage[4];
       Boolean[] tempStates = new Boolean[7];
       
        tempImages[0] = bufferedImage;
        tempImages[1] = edited;
        tempImages[2] = cropedPart;
        tempImages[3] = bufferedImage;

        tempStates[0] = isBlured;
        tempStates[1] = isReadyToSave;
        tempStates[2] = isChanged;
        tempStates[3] = isInverted;
        tempStates[4] = isRectangularCrop;
        tempStates[5] = isCircularCrop;
        tempStates[6] = isImageLoaded;
        
        changeAction();
        
        imageCopy[0] = tempImages[0];
        imageCopy[1] = tempImages[1];
        imageCopy[2] = tempImages[2];
        imageCopy[3] = tempImages[3];

        statesCopy[0] = tempStates[0];
        statesCopy[1] = tempStates[1];
        statesCopy[2] = tempStates[2];
        statesCopy[3] = tempStates[3];
        statesCopy[4] = tempStates[4];
        statesCopy[5] = tempStates[5];
        statesCopy[6] = tempStates[6];
        
        repaint();
        
   }
   
}