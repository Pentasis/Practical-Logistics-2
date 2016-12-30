package sonar.logistics.client;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import sonar.core.helpers.RenderHelper;
import sonar.logistics.Logistics;
import sonar.logistics.common.tileentity.TileEntityHammer;

public class RenderHammer extends TileEntitySpecialRenderer {
	
	public final static String modelFolder = Logistics.MODID + ":textures/model/";
	public ModelHammer model = new ModelHammer();
	public String texture = modelFolder + "hammer.png";
	public String textureNew = modelFolder + "hammer_machine.png";
	public ResourceLocation rope = new ResourceLocation(modelFolder + "rope.png");

	@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float f, int par) {
		RenderHelper.beginRender(x + 0.5F, y + 1.5F, z + 0.5F, RenderHelper.setMetaData(entity), textureNew);
		int progress = 0;
		boolean cooling = false;
		if (entity != null && entity.getWorld() != null) {
			TileEntityHammer hammer = (TileEntityHammer) entity;
			if (hammer.coolDown.getObject() != 0) {
				progress = hammer.coolDown.getObject();
				cooling = true;
			} else
				progress = hammer.progress.getObject();
			// double move = progress * 1.625 / hammer.speed;
			double move = !cooling ? progress * 1.625 / hammer.speed : progress * 1.625 / 200;
			model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F, true, move);
		} else {
			model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F, false, 0);
		}
		RenderHelper.finishRender();
		if (entity.getWorld() != null) {
			GL11.glTranslated(0, 2.75, 0);
			double height = -(!cooling ? progress * 1.625 / 100 : progress * 1.625 / 200);
			// double height = -(progress * 1.625 / 100);
			float width = 0.53F;
			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer vertexbuffer = tessellator.getBuffer();
			this.bindTexture(rope);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
			GL11.glTranslated(0.0, 0.70, 0.0);
			float f2 = 20;
			float f4 = -f2 * 0.2F - (float) MathHelper.floor_float(-f2 * 0.1F);
			byte b0 = 1;
			double d3 = (double) f2 * 0.025D * (1.0D - (double) (b0 & 1) * 2.5D);
			GL11.glTranslated(0.0, -0.70, 0.0);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glDepthMask(true);
			vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);

			double remain = 1 - width;
			double offset = 0.2D - 1 / 4;
			double d18 = height;
			double d20 = 0.0D;
			double d22 = 1.0D;
			double d24 = (double) (-1.0F + f4);
			double d26 = d18 + d24;

			RenderHelper.addVertexWithUV(vertexbuffer, x + remain, y + d18, z + remain, d22, d26);
			RenderHelper.addVertexWithUV(vertexbuffer, x + remain, y, z + remain, d22, d24);
			RenderHelper.addVertexWithUV(vertexbuffer, x + width, y, z + remain, d20, d24);
			RenderHelper.addVertexWithUV(vertexbuffer, x + width, y + d18, z + remain, d20, d26);
			RenderHelper.addVertexWithUV(vertexbuffer, x + width, y + d18, z + width, d22, d26);
			RenderHelper.addVertexWithUV(vertexbuffer, x + width, y, z + width, d22, d24);
			RenderHelper.addVertexWithUV(vertexbuffer, x + remain, y, z + width, d20, d24);
			RenderHelper.addVertexWithUV(vertexbuffer, x + remain, y + d18, z + width, d20, d26);
			RenderHelper.addVertexWithUV(vertexbuffer, x + width, y + d18, z + remain, d22, d26);
			RenderHelper.addVertexWithUV(vertexbuffer, x + width, y, z + remain, d22, d24);
			RenderHelper.addVertexWithUV(vertexbuffer, x + width, y, z + width, d20, d24);
			RenderHelper.addVertexWithUV(vertexbuffer, x + width, y + d18, z + width, d20, d26);
			RenderHelper.addVertexWithUV(vertexbuffer, x + remain, y + d18, z + width, d22, d26);
			RenderHelper.addVertexWithUV(vertexbuffer, x + remain, y, z + width, d22, d24);
			RenderHelper.addVertexWithUV(vertexbuffer, x + remain, y, z + remain, d20, d24);
			RenderHelper.addVertexWithUV(vertexbuffer, x + remain, y + d18, z + remain, d20, d26);

			tessellator.draw();
			GL11.glDepthMask(true);
			GL11.glTranslated(0, -2.75, 0);
		}

		int[] sides = new int[6];
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);

		if (entity != null && entity.getWorld() != null && entity instanceof TileEntityHammer) {
			TileEntityHammer hammer = (TileEntityHammer) entity;
			ItemStack target = null;
			if ((progress == 0 || cooling) && hammer.getStackInSlot(1) != null) {
				target = hammer.getStackInSlot(1);
			} else if (!cooling) {
				target = hammer.getStackInSlot(0);
			}

			if (target != null) {
				if (!(target.getItem() instanceof ItemBlock)) {
					GL11.glRotated(90, 1, 0, 0);
					GL11.glTranslated(0.0625 * 8, 0.3, -0.885);

				} else {
					GL11.glRotated(90, -1, 0, 0);
					GL11.glTranslated(0.5, -0.7, 0.92);
					if (!cooling && progress > 81 || cooling && (progress / 2) - 10 > 81) {
						if (cooling) {
							progress = (progress / 2) - 10;
						}
						GL11.glTranslated(0, 0, -((progress - 81) * 0.085 / (hammer.speed - 81)));
						GL11.glScaled(1, 1, 1 - ((progress - 81) * 0.85 / (hammer.speed - 81)));
					}
				}
				// RenderHelper.renderItem(entity.getWorld(), target);
			}
		}
		GL11.glPopMatrix();
	}
}