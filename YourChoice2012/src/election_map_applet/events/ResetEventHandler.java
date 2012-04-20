package election_map_applet.events;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import election_map_applet.ElectionMapDataModel;

public class ResetEventHandler implements MouseListener{
	private ElectionMapDataModel dataModel;
	private int i=0;
	public ResetEventHandler(ElectionMapDataModel dataModel){
		this.dataModel= dataModel;
	}
	public void mouseClicked(MouseEvent e) {
		i++;
		this.dataModel.setCurrentYear(i);
		this.dataModel.getRenderer().repaint();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}

}
