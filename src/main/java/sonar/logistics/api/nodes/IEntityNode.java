package sonar.logistics.api.nodes;

import java.util.List;

import net.minecraft.entity.Entity;

/** implemented by Entity Nodes, provides a list of all the entities they are connected to, normally just one, but can be more */
public interface IEntityNode {

	public void addEntities(List<Entity> entities);
}
