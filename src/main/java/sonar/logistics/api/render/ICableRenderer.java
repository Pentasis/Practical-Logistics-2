package sonar.logistics.api.render;

import net.minecraft.util.EnumFacing;
import sonar.logistics.api.cabling.ConnectableType;

/**used by tiles which render cables, this includes Cables themselves*/
public interface ICableRenderer {

	/**used by the client to check if the cable can connect, if it can it will render the connection*/
    ConnectableType canRenderConnection(EnumFacing dir);
}
