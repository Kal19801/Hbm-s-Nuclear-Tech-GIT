package api.hbm.energymk2;

import com.hbm.packet.AuxParticlePacketNT;
import com.hbm.packet.PacketDispatcher;

import api.hbm.energymk2.Nodespace.PowerNode;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public interface IEnergyProviderMK2 extends IEnergyConnectorMK2 {
	
	public void setPower(long power);
	
	public default void tryProvide(World world, int x, int y, int z, ForgeDirection dir) {

		TileEntity te = world.getTileEntity(x, y, z);
		boolean red = false;
		
		if(te instanceof IEnergyConductorMK2) {
			IEnergyConductorMK2 con = (IEnergyConductorMK2) te;
			if(!con.canConnect(dir.getOpposite())) return;
			
			PowerNode node = Nodespace.getNode(world, x, y, z);
			
			if(node != null && node.net != null) {
				node.net.addProvider(this);
				red = true;
			}
		}
		
		//TODO: direct transfer
		
		if(particleDebug) {
			NBTTagCompound data = new NBTTagCompound();
			data.setString("type", "network");
			data.setString("mode", "power");
			double posX = x + 0.5 - dir.offsetX * 0.5 + world.rand.nextDouble() * 0.5 - 0.25;
			double posY = y + 0.5 - dir.offsetY * 0.5 + world.rand.nextDouble() * 0.5 - 0.25;
			double posZ = z + 0.5 - dir.offsetZ * 0.5 + world.rand.nextDouble() * 0.5 - 0.25;
			data.setDouble("mX", dir.offsetX * (red ? 0.025 : 0.1));
			data.setDouble("mY", dir.offsetY * (red ? 0.025 : 0.1));
			data.setDouble("mZ", dir.offsetZ * (red ? 0.025 : 0.1));
			PacketDispatcher.wrapper.sendToAllAround(new AuxParticlePacketNT(data, posX, posY, posZ), new TargetPoint(world.provider.dimensionId, posX, posY, posZ, 25));
		}
	}
}
