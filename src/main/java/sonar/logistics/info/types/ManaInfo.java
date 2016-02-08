package sonar.logistics.info.types;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;

import org.lwjgl.opengl.GL11;

import sonar.logistics.api.Info;
import sonar.logistics.api.render.InfoRenderer;
import sonar.logistics.client.renderers.RenderHandlers;
import sonar.logistics.info.providers.tile.ManaProvider;

public class ManaInfo extends ProgressInfo {

	private static final ResourceLocation progress = new ResourceLocation(RenderHandlers.modelFolder + "progressBar.png");
	//public long stored, max;
	public int providerID = -1;

	public ManaInfo() {
	}

	public ManaInfo(int providerID, long stored, long max) {
		this.stored = stored;
		this.max = max;
		this.providerID = providerID;
		this.data = " ";
	}

	@Override
	public String getName() {
		return "ManaBar";
	}

	@Override
	public byte getProviderID() {
		return (byte) providerID;
	}

	@Override
	public String getCategory() {
		return new ManaProvider().getCategory((byte) 0);
	}

	@Override
	public String getSubCategory() {
		return "Mana";
	}

	@Override
	public String getData() {
		return String.valueOf(stored);
	}

	@Override
	public String getDisplayableData() {
		return "-";
	}

	@Override
	public int getDataType() {
		return 1;
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		this.stored = buf.readLong();
		this.max = buf.readLong();
		this.providerID = buf.readByte();
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		buf.writeLong(stored);
		buf.writeLong(max);
		buf.writeByte(providerID);

	}

	public void readFromNBT(NBTTagCompound tag) {
		this.stored = tag.getLong("stored");
		this.max = tag.getLong("max");
		this.providerID = tag.getByte("ID");
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setLong("stored", stored);
		tag.setLong("max", max);
		tag.setByte("ID", (byte) providerID);
	}

	@Override
	public void renderInfo(Tessellator tess, TileEntity tile, float minX, float minY, float maxX, float maxY, float zOffset, float scale) {
		/*
		 * FontRenderer rend = Minecraft.getMinecraft().fontRenderer;
		 * GL11.glTranslated(0, 0, zOffset); float width = stored * (maxX -
		 * minX) / max;
		 * Minecraft.getMinecraft().renderEngine.bindTexture(progress);
		 * tess.startDrawingQuads();
		 * 
		 * double widthnew = (0 + (width * (2))); double heightnew = (0 + ((maxY
		 * - minY) * (2)));
		 * 
		 * tess.addVertexWithUV((minX + 0), maxY/2, 0, 0, heightnew);
		 * tess.addVertexWithUV((minX + width), maxY/2, 0, widthnew, heightnew);
		 * tess.addVertexWithUV((minX + width), (minY + 0), 0, widthnew, 0);
		 * tess.addVertexWithUV((minX + 0), (minY + 0), 0, 0, 0);
		 * 
		 * tess.draw();
		 * 
		 * GL11.glTranslatef(minX + (maxX - minX) / 2, minY+(maxY-minY) / 2,
		 * 0.01f); int sizing = Math.round(Math.min((maxX - minX),
		 * (maxY-minY)*3)); GL11.glTranslatef(0.0f, (float) (scale >= 120 ?
		 * -0.08F : -0.2F + ((sizing-1)*-0.01)), 0); double itemScale =
		 * sizing>=2 ? InfoRenderer.getScale(sizing) : 120; GL11.glScaled(1.0f /
		 * itemScale, 1.0f / itemScale, 1.0f / itemScale); rend.drawString(data,
		 * -rend.getStringWidth(data) / 2, -4, -1);
		 */
		FontRenderer rend = Minecraft.getMinecraft().fontRenderer;
		GL11.glTranslated(0, 0, zOffset);
		float width = stored * (maxX - minX) / max;
		float height = stored * (maxY - minY) / max;
		Minecraft.getMinecraft().renderEngine.bindTexture(progress);
		tess.startDrawingQuads();

		double widthnew = (0 + (width * (2)));
		double heightnew = (0 + ((maxY - minY) * (2)));

		tess.addVertexWithUV((minX + 0), maxY / 2, 0, 0, heightnew);
		tess.addVertexWithUV((minX + width), maxY / 2, 0, widthnew, heightnew);
		tess.addVertexWithUV((minX + width), (minY + 0), 0, widthnew, 0);
		tess.addVertexWithUV((minX + 0), (minY + 0), 0, 0, 0);

		tess.draw();

		GL11.glTranslatef(minX + (maxX - minX) / 2, minY + (maxY - minY) / 2, 0.01f);
		int sizing = Math.round(Math.min((maxX - minX), (maxY - minY) * 3));
		GL11.glTranslatef(0.0f, (float) (scale >= 120 ? -0.08F : -0.2F + ((sizing - 1) * -0.01)), 0);
		double itemScale = sizing >= 2 ? InfoRenderer.getScale(sizing) : 120;
		GL11.glScaled(1.0f / itemScale, 1.0f / itemScale, 1.0f / itemScale);
		rend.drawString(this.getSubCategory(), -rend.getStringWidth(this.getSubCategory()) / 2, -4, -1);
	}
	
	@Override
	public ManaInfo instance() {
		return new ManaInfo();
	}

}
