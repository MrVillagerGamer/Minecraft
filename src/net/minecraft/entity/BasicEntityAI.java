package net.minecraft.entity;

import net.minecraft.util.Transform;
import net.minecraft.util.Vector2f;

public class BasicEntityAI extends EntityAI{
	private boolean rotateInProgress = false, rotateTowardInProgress = false;
	private int rotationDir = 1;
	private Transform target;
	private CreatureEntity entity;
	public BasicEntityAI(CreatureEntity entity, Transform target) {
		this.target = target;
		this.entity = entity;
	}
	@Override
	public void tick(float delta) {
		Vector2f diff = new Vector2f();
		diff.x = entity.transform.position.x - target.position.x;
		diff.y = entity.transform.position.z - target.position.z;
		float len = (float) Math.sqrt(diff.x*diff.x+diff.y*diff.y);
		diff.x /= len;
		diff.y /= len;
		float angle = (float) Math.toDegrees(Math.atan2(diff.y, -diff.x));
		angle += 360;
		angle %= 360;
		
		entity.transform.rotation.y %= 360;
		if(!entity.check(angle)) {
			rotateInProgress = false;
			rotateTowardInProgress = true;
			rotationDir = angle>entity.transform.rotation.y?1:-1;
			entity.moveDir = 0;
			if((int)angle == (int)entity.transform.rotation.y) {
				rotateTowardInProgress = false;
				rotationDir = 0;
			}
		}
		if(!rotateInProgress && !rotateTowardInProgress) {
			entity.moveDir = 1;
		}
		Transform transform = entity.transform.copy();
		transform.position.y+=1;
		boolean blocked1 = entity.check(transform, entity.transform.rotation.y);
		transform = entity.transform.copy();
		transform.position.y-=2;
		boolean blocked2 = !entity.check(transform, entity.transform.rotation.y);
		if(blocked1 || blocked2) {
			if(!rotateInProgress) {
				rotationDir = rotationDir>0?-1:1;
			}
			rotateInProgress = true;
			rotateTowardInProgress = false;
			if(blocked2) {
				entity.moveDir = -1;
			}
		}else {
			rotateInProgress = false;
		}
		if(rotateInProgress) {
			entity.transform.rotation.y += rotationDir * delta*entity.turnSpeed;
		}
		if(rotateTowardInProgress) {
			entity.transform.rotation.y += rotationDir * delta*entity.turnSpeed;
		}
	}
}
