package election_map_applet.events;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import election_map_applet.ElectionMapDataModel;

/**
 * This event handler responds to key presses, like when the user
 * presses ESC while viewing a state map.
 * 
 * @author Richard McKenna, Aaron Meltzer
 */
public class ElectionMapKeyHandler implements KeyListener
{
	// THE DATA MODEL WILL PROVIDE THE APPROPRIATE RESPONSE
	private ElectionMapDataModel dataModel;

	/**
	 * We must have the data model when we construct this object.
	 * @param initDataModel:ElectionMapDataModel
	 */
	public ElectionMapKeyHandler(ElectionMapDataModel initDataModel)
	{
		dataModel = initDataModel;
	}

	/**
	 * Here is where we respond to key presses. Note that in this
	 * application we only care about key presses, not releases.
	 * @param ke:KeyEvent
	 */
	public void keyPressed(KeyEvent ke) 
	{
		// STORES THE KEY PRESSED FOR THIS EVENT
		int key = ke.getKeyCode();
		if(key==27){
			dataModel.getRenderer().zoomHandler("USA");
		}
	}

	// THESE ARE NOT USED
	public void keyReleased(KeyEvent ke) 	{}
	public void keyTyped(KeyEvent ke) 		{}	
}