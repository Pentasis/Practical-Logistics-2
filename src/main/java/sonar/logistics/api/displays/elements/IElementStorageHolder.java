package sonar.logistics.api.displays.elements;

import net.minecraft.util.Tuple;
import sonar.logistics.api.displays.IDisplayElement;

public interface IElementStorageHolder {

	ElementStorage getElements();
	
	DisplayElementContainer getContainer();
	
	double[] getAlignmentTranslation();
	
	double[] getAlignmentTranslation(IDisplayElement e);
	
	void startElementRender(IDisplayElement e);
	
	void endElementRender(IDisplayElement e);
	
	Tuple<IDisplayElement, double[]> getClickBoxes(double x, double y);

	void onElementAdded(IDisplayElement element);

	void onElementRemoved(IDisplayElement element);
	
	double[] getMaxScaling();	
	
	double[] getActualScaling();

	double[] createMaxScaling(IDisplayElement element);

	double[] createActualScaling(IDisplayElement element);
	
	void updateActualScaling(); 
	
}