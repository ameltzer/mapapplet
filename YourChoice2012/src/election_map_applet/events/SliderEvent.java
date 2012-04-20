package election_map_applet.events;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import election_map_applet.ElectionMapDataModel;

public class SliderEvent implements ChangeListener {
	private ElectionMapDataModel dataModel;
	int i=0;
	public SliderEvent(ElectionMapDataModel dataModel){
		this.dataModel=dataModel;
	}
	public void stateChanged(ChangeEvent e) {
		i++;
		this.dataModel.setCurrentYear(i);
		this.dataModel.getRenderer().repaint();
		
	}

}
