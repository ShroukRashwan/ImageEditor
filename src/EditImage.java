import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;



/**
 * @author ShroukRashwan
 *
 */

public class EditImage extends JFrame implements ActionListener{

	/**
	 * Default Serial ID
	 */
	private static final long serialVersionUID = 1L;
	
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
	
	
	String imagePath;
	
	// create instance of Image Controller
	ImageController controller;
	// create container to add image into window
	Container cont = new Container();
	
	/**
	 * Public Constructor
	 */
	public EditImage()
	{
		controller = new ImageController();
		
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
		controller.requestFocus();
		
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		
			new EditImage();
	}
	
	/**
	 *  Function to get the image path from user 
	 */
	
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
	public void actionPerformed(ActionEvent e) 
	{
		
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
            controller.saveActions();
        } 
	}
	else if (0 == actionSource.getText().compareTo("Invert Image"))
	{
        if(controller.isImageLoaded())
        {
            controller.setIsReadyToSave(true);
            controller.setInverted(true);
            controller.invertImage();
            controller.saveActions();
        } 
        }
        else if (0 == actionSource.getText().compareTo("Change Image Light Level"))
        {
            ImageBrightness ib=new ImageBrightness(); 
            if(controller.isImageLoaded()) 
            {
			    controller.setIsReadyToSave(true);
			    ib.enableSlider(true); 
                controller.saveActions();
            }
        }
        else if (0 == actionSource.getText().compareTo("Select Circular Area"))
        {
                // Not Implemented
        }
        else if (0 == actionSource.getText().compareTo("Select Rectangular Area"))
        {
                /* put in generic place and change the drawn shape according to option */
                controller.setRectangularCrop(true);
                controller.saveActions();
        }
        else if (0 == actionSource.getText().compareTo("Undo Last Action"))
        {
            if(controller.isImageLoaded()) 
            {
                controller.UndoRedoAction();
                controller.setDidUndo(true);
            }
        }
        else if (0 == actionSource.getText().compareTo("Redo Last Action"))
        {
            if (controller.didUndo && controller.isImageLoaded())
            {
                controller.UndoRedoAction();
                controller.setDidUndo(false);
            }
        }
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
	
	
	/**
	 * @author ShroukRashwan
	 * Class to control Brightness Slider
	 */
	public class ImageBrightness extends JFrame implements ChangeListener
	{
		/**
		 * Default Serial ID
		 */
		private static final long serialVersionUID = 1L;
		JSlider slider;
		 
		/**
		 * Public Constructor
		 */
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
		}
	}


	
}