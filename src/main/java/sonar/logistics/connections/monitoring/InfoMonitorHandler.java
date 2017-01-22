package sonar.logistics.connections.monitoring;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.api.utils.BlockCoords;
import sonar.logistics.Logistics;
import sonar.logistics.api.asm.MonitorHandler;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.info.ICustomTileHandler;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.info.monitor.LogicMonitorHandler;
import sonar.logistics.info.LogicInfoRegistry;

@MonitorHandler(handlerID = InfoMonitorHandler.id, modid = Logistics.MODID)
public class InfoMonitorHandler extends LogicMonitorHandler<LogicInfo> {

	public static final String id = "info";
	
	@Override
	public String id() {
		return id;
	}

	@Override
	public MonitoredList<LogicInfo> updateInfo(INetworkCache network, MonitoredList<LogicInfo> previousList, BlockCoords coords, EnumFacing dir) {
		MonitoredList<LogicInfo> list = MonitoredList.<LogicInfo>newMonitoredList(network.getNetworkID());
		EnumFacing face = dir.getOpposite();World world =coords.getWorld(); IBlockState state = coords.getBlockState(world); BlockPos pos = coords.getBlockPos(); Block block = coords.getBlock(world); TileEntity tile = coords.getTileEntity(world);
		LogicInfoRegistry.getTileInfo(list,face, world, state, pos, face, block, tile);		
		for(ICustomTileHandler handler : LogicInfoRegistry.customTileHandlers){
			if(handler.canProvideInfo(world, state, pos, face, tile, block)){
				handler.addInfo(list, world, state, pos, face, tile, block);
			}
		}		
		return list;
	}
}
