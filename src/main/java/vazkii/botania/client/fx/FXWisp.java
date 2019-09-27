/**
 * This class was created by <Azanor>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 * File Created @ [? (GMT)]
 */
package vazkii.botania.client.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import vazkii.botania.client.lib.LibResources;
import vazkii.botania.common.core.handler.ConfigHandler;

import javax.annotation.Nonnull;
import java.util.ArrayDeque;
import java.util.Queue;

public class FXWisp extends Particle {

	private static final ResourceLocation vanillaParticles = new ResourceLocation("textures/particle/particles.png");
	public static final ResourceLocation particles = new ResourceLocation(LibResources.MISC_WISP_LARGE);

	private static final Queue<FXWisp> queuedRenders = new ArrayDeque<>();
	private static final Queue<FXWisp> queuedDepthIgnoringRenders = new ArrayDeque<>();

	protected float particleScale = (this.rand.nextFloat() * 0.5F + 0.5F) * 2.0F;
	// Queue values
	private float f;
	private float f1;
	private float f2;
	private float f3;
	private float f4;
	private float f5;

	public FXWisp(World world, double d, double d1, double d2, double xSpeed, double ySpeed, double zSpeed,
				  float size, float red, float green, float blue, boolean depthTest, float maxAgeMul) {
		super(world, d, d1, d2, 0, 0, 0);
		// super applies wiggle to motion so set it here instead
		motionX = xSpeed;
		motionY = ySpeed;
		motionZ = zSpeed;
		particleRed = red;
		particleGreen = green;
		particleBlue = blue;
		particleGravity = 0;
		particleScale *= size;
		moteParticleScale = particleScale;
		maxAge = (int)(28D / (Math.random() * 0.3D + 0.7D) * maxAgeMul);
		this.depthTest = depthTest;

		moteHalfLife = maxAge / 2;
		setSize(0.01F, 0.01F);

		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
	}

	public static void dispatchQueuedRenders(Tessellator tessellator) {
		ParticleRenderDispatcher.wispFxCount = 0;
		ParticleRenderDispatcher.depthIgnoringWispFxCount = 0;

		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.75F);
		Minecraft.getInstance().textureManager.bindTexture(ConfigHandler.CLIENT.matrixMode.get() ? vanillaParticles : particles);

		if(!queuedRenders.isEmpty()) {
			tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
			for(FXWisp wisp : queuedRenders)
				wisp.renderQueued(tessellator, true);
			tessellator.draw();
		}

		if(!queuedDepthIgnoringRenders.isEmpty()) {
			GlStateManager.disableDepthTest();
			tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
			for(FXWisp wisp : queuedDepthIgnoringRenders)
				wisp.renderQueued(tessellator, false);
			tessellator.draw();
			GlStateManager.enableDepthTest();
		}

		queuedRenders.clear();
		queuedDepthIgnoringRenders.clear();
	}

	private void renderQueued(Tessellator tessellator, boolean depthEnabled) {
		if(depthEnabled)
			ParticleRenderDispatcher.wispFxCount++;
		else ParticleRenderDispatcher.depthIgnoringWispFxCount++;

		float agescale = (float)age / (float) moteHalfLife;
		if (agescale > 1F)
			agescale = 2 - agescale;

		particleScale = moteParticleScale * agescale;

		float f10 = 0.5F * particleScale;
		float f11 = (float)(prevPosX + (posX - prevPosX) * f - interpPosX);
		float f12 = (float)(prevPosY + (posY - prevPosY) * f - interpPosY);
		float f13 = (float)(prevPosZ + (posZ - prevPosZ) * f - interpPosZ);
		int combined = 15 << 20 | 15 << 4;
		int k3 = combined >> 16 & 0xFFFF;
		int l3 = combined & 0xFFFF;
		tessellator.getBuffer().pos(f11 - f1 * f10 - f4 * f10, f12 - f2 * f10, f13 - f3 * f10 - f5 * f10).tex(0, 1).lightmap(k3, l3).color(particleRed, particleGreen, particleBlue, 0.5F).endVertex();
		tessellator.getBuffer().pos(f11 - f1 * f10 + f4 * f10, f12 + f2 * f10, f13 - f3 * f10 + f5 * f10).tex(1, 1).lightmap(k3, l3).color(particleRed, particleGreen, particleBlue, 0.5F).endVertex();
		tessellator.getBuffer().pos(f11 + f1 * f10 + f4 * f10, f12 + f2 * f10, f13 + f3 * f10 + f5 * f10).tex(1, 0).lightmap(k3, l3).color(particleRed, particleGreen, particleBlue, 0.5F).endVertex();
		tessellator.getBuffer().pos(f11 + f1 * f10 - f4 * f10, f12 - f2 * f10, f13 + f3 * f10 - f5 * f10).tex(0, 0).lightmap(k3, l3).color(particleRed, particleGreen, particleBlue, 0.5F).endVertex();
	}

	@Override
	public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		this.f = partialTicks;
		this.f1 = rotationX;
		this.f2 = rotationZ;
		this.f3 = rotationYZ;
		this.f4 = rotationXY;
		this.f5 = rotationXZ;

		if(depthTest)
			queuedRenders.add(this);
		else queuedDepthIgnoringRenders.add(this);
	}

	@Nonnull
	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.CUSTOM;
	}

	// [VanillaCopy] of super, without drag when onGround is true
	@Override
	public void tick() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if (this.age++ >= this.maxAge)
		{
			this.setExpired();
		}

		this.motionY -= 0.04D * (double)this.particleGravity;
		this.move(this.motionX, this.motionY, this.motionZ);
		this.motionX *= 0.9800000190734863D;
		this.motionY *= 0.9800000190734863D;
		this.motionZ *= 0.9800000190734863D;
	}

	public void setGravity(float value) {
		particleGravity = value;
	}

	public void setSpeed(float mx, float my, float mz) {
		motionX = mx;
		motionY = my;
		motionZ = mz;
	}

	private boolean depthTest;
	private final float moteParticleScale;
	private final int moteHalfLife;
}
