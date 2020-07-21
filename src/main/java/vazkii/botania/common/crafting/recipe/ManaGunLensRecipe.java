/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import vazkii.botania.api.mana.ILens;
import vazkii.botania.api.mana.ILensControl;
import vazkii.botania.common.item.ItemManaGun;

import javax.annotation.Nonnull;

public class ManaGunLensRecipe extends SpecialCraftingRecipe {
	public static final SpecialRecipeSerializer<ManaGunLensRecipe> SERIALIZER = new SpecialRecipeSerializer<>(ManaGunLensRecipe::new);

	public ManaGunLensRecipe(Identifier id) {
		super(id);
	}

	@Override
	public boolean matches(@Nonnull CraftingInventory inv, @Nonnull World world) {
		boolean foundLens = false;
		boolean foundGun = false;

		for (int i = 0; i < inv.size(); i++) {
			ItemStack stack = inv.getStack(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof ItemManaGun && ItemManaGun.getLens(stack).isEmpty()) {
					foundGun = true;
				} else if (stack.getItem() instanceof ILens) {
					if (!(stack.getItem() instanceof ILensControl) || !((ILensControl) stack.getItem()).isControlLens(stack)) {
						foundLens = true;
					} else {
						return false;
					}
				}

				else {
					return false; // Found an invalid item, breaking the recipe
				}
			}
		}

		return foundLens && foundGun;
	}

	@Nonnull
	@Override
	public ItemStack craft(@Nonnull CraftingInventory inv) {
		ItemStack lens = ItemStack.EMPTY;
		ItemStack gun = ItemStack.EMPTY;

		for (int i = 0; i < inv.size(); i++) {
			ItemStack stack = inv.getStack(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof ItemManaGun) {
					gun = stack;
				} else if (stack.getItem() instanceof ILens) {
					lens = stack;
				}
			}
		}

		if (lens.isEmpty() || gun.isEmpty()) {
			return ItemStack.EMPTY;
		}

		ItemStack gunCopy = gun.copy();
		ItemManaGun.setLens(gunCopy, lens);

		return gunCopy;
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= 2;
	}

	@Nonnull
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
}
