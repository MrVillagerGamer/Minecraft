package net.minecraft.entity;

import net.minecraft.Main;
import net.minecraft.util.RenderUtil;
import net.minecraft.util.Transform;
import net.minecraft.util.Vector2f;
import net.minecraft.util.Vector3f;
import net.minecraft.util.Vector4f;

public class ZombieEntity extends CreatureEntity {
	BasicEntityAI ai;
	public ZombieEntity() {
		usePhysics = false;
		parts = new EntityPart[6];
		moveSpeed = 2;
		turnSpeed = 180;
		jumpForce = 5;
		
		Vector2f uv = RenderUtil.textureIndexToUV(16*15+3, new Vector2f(0, 0));
		float t = 1f/16f/RenderUtil.ATLAS_SIZE;
		float s = 1f/16f;
		transform.scale = new Vector3f(0.3f, s*32f, 0.3f);
		Vector4f uvst4x12 = new Vector4f(uv.x, uv.y, uv.x+t*4, uv.y+t*12);
		Vector4f uvst8x12 = new Vector4f(uv.x, uv.y, uv.x+t*8, uv.y+t*12);
		Vector4f uvst4x4 = new Vector4f(uv.x, uv.y, uv.x+t*4, uv.y+t*4);
		Vector4f uvst4x8 = new Vector4f(uv.x, uv.y, uv.x+t*4, uv.y+t*8);
		Vector4f uvst8x8 = new Vector4f(uv.x, uv.y, uv.x+t*8, uv.y+t*8);
		Transform leftLegT = new Transform();
		Transform rightLegT = new Transform();
		Transform leftArmT = new Transform();
		Transform rightArmT = new Transform();
		Transform headT = new Transform();
		Transform bodyT = new Transform();
		leftLegT.position = new Vector3f(0, -s*10f, -s*2f);
		rightLegT.position = new Vector3f(0, -s*10f, s*2f);
		leftArmT.position = new Vector3f(0, s*2f, -s*6f);
		rightArmT.position = new Vector3f(0, s*2f, s*6f);
		bodyT.position = new Vector3f(0, s*2f, 0);
		headT.position = new Vector3f(0, s*12f, 0);
		leftLegT.scale = new Vector3f(s*4, s*12, s*4);
		rightLegT.scale = new Vector3f(s*4, s*12, s*4);
		leftArmT.scale = new Vector3f(s*4, s*12, s*4);
		rightArmT.scale = new Vector3f(s*4, s*12, s*4);
		leftArmT.rotation = new Vector3f(0, 0, 90);
		rightArmT.rotation = new Vector3f(0, 0, 90);
		bodyT.scale = new Vector3f(s*4f, s*12f, s*8f);
		headT.scale = new Vector3f(s*8f, s*8f, s*8f);
		
		EntityPart leftLeg = new EntityPart(leftLegT, new Vector3f(0, s*6, 0), new Vector4f[] {uvst4x12, uvst4x12, uvst4x4, uvst4x4, uvst4x12, uvst4x12});
		EntityPart rightLeg = new EntityPart(rightLegT, new Vector3f(0, s*6, 0), new Vector4f[] {uvst4x12, uvst4x12, uvst4x4, uvst4x4, uvst4x12, uvst4x12});
		EntityPart leftArm = new EntityPart(leftArmT, new Vector3f(0, s*6, 0), new Vector4f[] {uvst4x12, uvst4x12, uvst4x4, uvst4x4, uvst4x12, uvst4x12});
		EntityPart rightArm = new EntityPart(rightArmT, new Vector3f(0, s*6, 0), new Vector4f[] {uvst4x12, uvst4x12, uvst4x4, uvst4x4, uvst4x12, uvst4x12});
		EntityPart body = new EntityPart(bodyT, new Vector3f(0, 0, 0), new Vector4f[] {uvst8x12, uvst8x12, uvst4x8, uvst4x8, uvst4x12, uvst4x12});
		EntityPart head = new EntityPart(headT, new Vector3f(0, 0, 0), new Vector4f[] {uvst8x8, uvst8x8, uvst8x8, uvst8x8, uvst8x8, uvst8x8});
		parts[0] = leftLeg;
		parts[1] = rightLeg;
		parts[2] = leftArm;
		parts[3] = rightArm;
		parts[4] = head;
		parts[5] = body;
	}
	@Override
	public void draw() {
		super.draw();
	}
	@Override
	public void load() {
		super.load();
		ai = new BasicEntityAI(this, Main.level.localPlayer.transform);
	}
	@Override
	public void unload() {
		super.unload();
	}
	float damageTime = 0;
	@Override
	public void tick(float delta) {
		ticking = true;
		if(active) {
			/*if(Main.level.localPlayer != null && Main.level.localPlayer instanceof CreatureEntity) {
				Vector3f dist = new Vector3f();
				dist.x = Main.level.localPlayer.transform.position.x;
				dist.y = Main.level.localPlayer.transform.position.y;
				dist.z = Main.level.localPlayer.transform.position.z;
				dist.x -= transform.position.x;
				dist.y -= transform.position.y;
				dist.z -= transform.position.z;
				if(dist.length() < 2) {
					if(damageTime >= 1f) {
						((CreatureEntity)Main.level.localPlayer).setHealth(((CreatureEntity)Main.level.localPlayer).getHealth()-1);
						damageTime -= 1f;
					}
					damageTime += delta;
				}else {
					damageTime = 0;
				}
			}
			ai.tick(delta);*/
		}
		super.tick(delta);
		ticking = false;
	}
}
