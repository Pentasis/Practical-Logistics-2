package sonar.logistics.integration.jei;
/*
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import sonar.core.integration.jei.JEISonarCategory;
import sonar.core.integration.jei.JEISonarMapper;
import sonar.core.integration.jei.JEISonarProvider;
import sonar.core.integration.jei.JEISonarRecipe;
import sonar.core.recipes.ISonarRecipe;
import sonar.core.recipes.RecipeHelperV2;
import sonar.core.recipes.RecipeObjectType;
import sonar.logistics.PL2Constants;

import javax.annotation.Nonnull;

public class ForgingHammerJEI extends JEISonarCategory {

	private final IDrawable background;
	protected final IDrawableAnimated arrow;

	public ForgingHammerJEI(IGuiHelper guiHelper, JEISonarProvider handler) {
		super(guiHelper, handler);
		ResourceLocation location = new ResourceLocation(PL2Constants.MODID, "textures/gui/" + handler.background + ".png");
		background = guiHelper.createDrawable(location, 48, 19, 80, 27);
		IDrawableStatic arrowDrawable = guiHelper.createDrawable(location, 177, 0, 22, 15);
		this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 100, IDrawableAnimated.StartDirection.LEFT, false);
	}

	@Nonnull
    @Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {
		arrow.draw(minecraft, 29, 5);
	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, IRecipeWrapper recipeWrapper, IIngredients ingredients) {
		JEISonarMapper mapper = new JEISonarMapper();
		mapper.map(RecipeObjectType.INPUT, 0, 0, 4, 4);
		mapper.map(RecipeObjectType.OUTPUT, 0, 1, 58, 4);
		mapper.mapTo(recipeLayout.getItemStacks(), ingredients);
	}

	public static class Hammer extends JEISonarRecipe<Hammer> {

		public Hammer(RecipeHelperV2 helper, ISonarRecipe recipe) {
			super(helper, recipe);
		}

	}
}*/