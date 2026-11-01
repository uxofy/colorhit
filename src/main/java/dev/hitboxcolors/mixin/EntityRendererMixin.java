package dev.hitboxcolors.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityHitbox;
import net.minecraft.client.render.entity.state.EntityHitboxAndView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {

    // Yarn 1.21.11: EntityRenderer#createHitbox(T entity, float tickProgress, boolean extended)
    @Inject(method = "createHitbox", at = @At("RETURN"), cancellable = true)
    private void hc$recolor(Entity entity, float tickProgress, boolean extended,
                            CallbackInfoReturnable<EntityHitboxAndView> cir) {
        EntityHitboxAndView original = cir.getReturnValue();
        if (original == null) return;

        float r, g, b;
        EntityType<?> type = entity.getType();
        if (type == EntityType.ENDER_PEARL) {
            r = 1.0F; g = 0.0F; b = 0.0F;          // red
        } else if (type == EntityType.WIND_CHARGE || type == EntityType.BREEZE_WIND_CHARGE) {
            r = 0.0F; g = 0.4F; b = 1.0F;          // blue
        } else {
            return;
        }

        ImmutableList.Builder<EntityHitbox> list = ImmutableList.builder();
        for (EntityHitbox h : original.hitboxes()) {
            list.add(new EntityHitbox(h.x0(), h.y0(), h.z0(), h.x1(), h.y1(), h.z1(),
                    h.offsetX(), h.offsetY(), h.offsetZ(), r, g, b));
        }
        cir.setReturnValue(new EntityHitboxAndView(
                original.viewX(), original.viewY(), original.viewZ(), list.build()));
    }
}
