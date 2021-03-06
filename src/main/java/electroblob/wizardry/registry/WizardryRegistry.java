package electroblob.wizardry.registry;

import java.util.ArrayList;
import java.util.List;

import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.entity.EntityArc;
import electroblob.wizardry.entity.EntityMeteor;
import electroblob.wizardry.entity.EntityShield;
import electroblob.wizardry.entity.construct.EntityArrowRain;
import electroblob.wizardry.entity.construct.EntityBlackHole;
import electroblob.wizardry.entity.construct.EntityBlizzard;
import electroblob.wizardry.entity.construct.EntityBubble;
import electroblob.wizardry.entity.construct.EntityDecay;
import electroblob.wizardry.entity.construct.EntityEarthquake;
import electroblob.wizardry.entity.construct.EntityFireRing;
import electroblob.wizardry.entity.construct.EntityFireSigil;
import electroblob.wizardry.entity.construct.EntityForcefield;
import electroblob.wizardry.entity.construct.EntityFrostSigil;
import electroblob.wizardry.entity.construct.EntityHailstorm;
import electroblob.wizardry.entity.construct.EntityHammer;
import electroblob.wizardry.entity.construct.EntityHealAura;
import electroblob.wizardry.entity.construct.EntityIceSpike;
import electroblob.wizardry.entity.construct.EntityLightningPulse;
import electroblob.wizardry.entity.construct.EntityLightningSigil;
import electroblob.wizardry.entity.construct.EntityTornado;
import electroblob.wizardry.entity.living.EntityBlazeMinion;
import electroblob.wizardry.entity.living.EntityDecoy;
import electroblob.wizardry.entity.living.EntityEvilWizard;
import electroblob.wizardry.entity.living.EntityIceGiant;
import electroblob.wizardry.entity.living.EntityIceWraith;
import electroblob.wizardry.entity.living.EntityLightningWraith;
import electroblob.wizardry.entity.living.EntityMagicSlime;
import electroblob.wizardry.entity.living.EntityPhoenix;
import electroblob.wizardry.entity.living.EntityShadowWraith;
import electroblob.wizardry.entity.living.EntitySilverfishMinion;
import electroblob.wizardry.entity.living.EntitySkeletonMinion;
import electroblob.wizardry.entity.living.EntitySpiderMinion;
import electroblob.wizardry.entity.living.EntitySpiritHorse;
import electroblob.wizardry.entity.living.EntitySpiritWolf;
import electroblob.wizardry.entity.living.EntityStormElemental;
import electroblob.wizardry.entity.living.EntityWizard;
import electroblob.wizardry.entity.living.EntityZombieMinion;
import electroblob.wizardry.entity.projectile.EntityDarknessOrb;
import electroblob.wizardry.entity.projectile.EntityDart;
import electroblob.wizardry.entity.projectile.EntityFirebolt;
import electroblob.wizardry.entity.projectile.EntityFirebomb;
import electroblob.wizardry.entity.projectile.EntityForceArrow;
import electroblob.wizardry.entity.projectile.EntityForceOrb;
import electroblob.wizardry.entity.projectile.EntityIceCharge;
import electroblob.wizardry.entity.projectile.EntityIceLance;
import electroblob.wizardry.entity.projectile.EntityIceShard;
import electroblob.wizardry.entity.projectile.EntityLightningArrow;
import electroblob.wizardry.entity.projectile.EntityLightningDisc;
import electroblob.wizardry.entity.projectile.EntityMagicMissile;
import electroblob.wizardry.entity.projectile.EntityPoisonBomb;
import electroblob.wizardry.entity.projectile.EntitySmokeBomb;
import electroblob.wizardry.entity.projectile.EntitySpark;
import electroblob.wizardry.entity.projectile.EntitySparkBomb;
import electroblob.wizardry.entity.projectile.EntityThunderbolt;
import electroblob.wizardry.loot.RandomSpell;
import electroblob.wizardry.loot.WizardSpell;
import electroblob.wizardry.tileentity.TileEntityArcaneWorkbench;
import electroblob.wizardry.tileentity.TileEntityMagicLight;
import electroblob.wizardry.tileentity.TileEntityPlayerSave;
import electroblob.wizardry.tileentity.TileEntityStatue;
import electroblob.wizardry.tileentity.TileEntityTimer;
import electroblob.wizardry.util.WizardryUtilities;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Class responsible for registering all the things that don't have (or need) instances: entities, loot tables,
 * recipes, etc.
 * @author Electroblob
 * @since Wizardry 1.0
 */
public final class WizardryRegistry {
	
	// NOTE: In 1.12, recipes have a registry (they can still stay here though since we don't keep references to them)
	
	/** Called from the preInit method in the main mod class to register the custom dungeon loot. */
	public static void registerLoot(){
		
		/* Loot tables work as follows:
		 * Minecraft goes through each pool in turn. For each pool, it does a certain number or rolls, which can either
		 * be set to always be one number or a random number from a range. Each roll, it generates one stack of a single
		 * random entry in that pool, weighted according to the weights of the entries. Functions allow properties of
		 * that stack (stack size, damage, nbt) to be set, and even allow it to be replaced dynamically with a
		 * completely different item (though there's very little point in doing that as it could be achieved just as
		 * easily with more entries, which makes me think it would be bad practice).
		 * You can also use conditions to control whether an entry or pool is used at all, which is mostly for mob drops
		 * under specific conditions, but one of them is simply a random chance, meaning you could use it to make a pool
		 * that only gets rolled sometimes.
		 * All in all, this can get rather confusing, because stackable items can have 5 stages of randomness applied
		 * to them at once: a random chance for the pool, a random number of rolls for the pool, the weighted random
		 * chance of choosing that particular entry, a random chance for that entry, and a random stack size, and that's
		 * before you take functions into account.
		 * 
		 * ...oh, and entries can be entire loot tables in themselves, allowing for potentially infinite levels of
		 * randomness. Yeah.
		 * 
		 * Translating to the new system:
		 * ChestGenHooks.SOME_NAME -> loot table json file
		 * ??? -> loot pool (I don't think it was split up like this before)
		 * ChestGenHooks.addItem() -> entry in a loot pool
		 * Stack sizes in WeightedRandomChestContent -> set_count function for entries
		 * Weight in WeightedRandomChestContent -> weight of entries
		 * Custom WeightedRandomChestContent implementations -> custom loot functions, but only for complex/dynamic
		 * stuff - things that serve to allow the chance for a category of items (like armour) to be specified but still
		 * have a random chance for which exact item you get should be done with nested loot tables.
		 */
		
		// Always registers the loot tables, but only injects the additions into vanilla if the appropriate option is
		// enabled in the config (see WizardryEventHandler).
		LootFunctionManager.registerFunction(new RandomSpell.Serializer());
		LootFunctionManager.registerFunction(new WizardSpell.Serializer());
		LootTableList.register(new ResourceLocation(Wizardry.MODID, "chests/wizard_tower"));
		LootTableList.register(new ResourceLocation(Wizardry.MODID, "chests/dungeon_additions"));
		LootTableList.register(new ResourceLocation(Wizardry.MODID, "subsets/novice_wands"));
		LootTableList.register(new ResourceLocation(Wizardry.MODID, "subsets/wizard_armour"));
		LootTableList.register(new ResourceLocation(Wizardry.MODID, "subsets/arcane_tomes"));
		LootTableList.register(new ResourceLocation(Wizardry.MODID, "subsets/wand_upgrades"));
		LootTableList.register(new ResourceLocation(Wizardry.MODID, "entities/evil_wizard"));
		// TODO: At the moment this is not used anywhere because I can't find a way to add it to all mobs.
		//LootTableList.register(new ResourceLocation(Wizardry.MODID, "entities/mob_additions"));
		
	}
	
	/** Called from the preInit method in the main mod class to register all the tile entities. */
	public static void registerTileEntities(){

        GameRegistry.registerTileEntity(TileEntityArcaneWorkbench.class, Wizardry.MODID + "ArcaneWorkbenchTileEntity");
        GameRegistry.registerTileEntity(TileEntityStatue.class, Wizardry.MODID + "PetrifiedStoneTileEntity");
        GameRegistry.registerTileEntity(TileEntityMagicLight.class, Wizardry.MODID + "MagicLightTileEntity");
        GameRegistry.registerTileEntity(TileEntityTimer.class, Wizardry.MODID + "TimerTileEntity");
        GameRegistry.registerTileEntity(TileEntityPlayerSave.class, Wizardry.MODID + "TileEntityPlayerSave");
	}
	
	/** Not actually the frequency at all; smaller numbers are more frequent. Vanilla uses 3 I think. */
	private static final int LIVING_UPDATE_INTERVAL = 3;
	/** Not actually the frequency at all; smaller numbers are more frequent. */
	private static final int PROJECTILE_UPDATE_INTERVAL = 10;
	
	/** Called from the preInit method in the main mod class to register all the entities. */
	public static void registerEntities(Wizardry wizardry){
		
		int id = 0; // Incrementable index for the mod specific entity id.
        
        EntityRegistry.registerModEntity(EntityZombieMinion.class, "zombie_minion", id++, wizardry, 128, LIVING_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntityMagicMissile.class, "magic_missile", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, true);
        // TODO: This should be a particle
        EntityRegistry.registerModEntity(EntityArc.class, "arc", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, false);
        EntityRegistry.registerModEntity(EntitySkeletonMinion.class, "skeleton_minion", id++, wizardry, 128, LIVING_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntitySparkBomb.class, "spark_bomb", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntitySpiritWolf.class, "spirit_wolf", id++, wizardry, 128, LIVING_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntityIceShard.class, "ice_shard", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntityBlazeMinion.class, "blaze_minion", id++, wizardry, 128, LIVING_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntityIceWraith.class, "ice_wraith", id++, wizardry, 128, LIVING_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntityLightningWraith.class, "lightning_wraith", id++, wizardry, 128, LIVING_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntityBlackHole.class, "black_hole", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, false);
        EntityRegistry.registerModEntity(EntityShield.class, "shield", id++, wizardry, 128, 1, true);
        EntityRegistry.registerModEntity(EntityMeteor.class, "meteor", id++, wizardry, 128, 5, true);
        EntityRegistry.registerModEntity(EntityBlizzard.class, "blizzard", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, false);
        EntityRegistry.registerModEntity(EntityWizard.class, "wizard", id++, wizardry, 128, LIVING_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntityBubble.class, "bubble", id++, wizardry, 128, 3, false);
        EntityRegistry.registerModEntity(EntityTornado.class, "tornado", id++, wizardry, 128, 1, false);
        EntityRegistry.registerModEntity(EntityHammer.class, "lightning_hammer", id++, wizardry, 128, 1, true);
        EntityRegistry.registerModEntity(EntityFirebomb.class, "firebomb", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntityForceOrb.class, "force_orb", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntityArrowRain.class, "arrow_rain", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, false);
        EntityRegistry.registerModEntity(EntitySpark.class, "spark", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntityShadowWraith.class, "shadow_wraith", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntityDarknessOrb.class, "darkness_orb", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntitySpiderMinion.class, "spider_minion", id++, wizardry, 128, LIVING_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntityHealAura.class, "healing_aura", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, false);
        EntityRegistry.registerModEntity(EntityFireSigil.class, "fire_sigil", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, false);
        EntityRegistry.registerModEntity(EntityFrostSigil.class, "frost_sigil", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, false);
        EntityRegistry.registerModEntity(EntityLightningSigil.class, "lightning_sigil", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, false);
        EntityRegistry.registerModEntity(EntityLightningArrow.class, "lightning_arrow", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntityFirebolt.class, "firebolt", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntityPoisonBomb.class, "poison_bomb", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntityIceCharge.class, "ice_charge", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntityForceArrow.class, "force_arrow", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntityDart.class, "dart", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntityMagicSlime.class, "magic_slime", id++, wizardry, 128, LIVING_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntityForcefield.class, "forcefield", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, false);
        EntityRegistry.registerModEntity(EntityFireRing.class, "ring_of_fire", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, false);
        EntityRegistry.registerModEntity(EntityLightningDisc.class, "lightning_disc", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntityThunderbolt.class, "thunderbolt", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntityIceGiant.class, "ice_giant", id++, wizardry, 128, LIVING_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntitySpiritHorse.class, "spirit_horse", id++, wizardry, 128, LIVING_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntityPhoenix.class, "phoenix", id++, wizardry, 128, LIVING_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntitySilverfishMinion.class, "silverfish_minion", id++, wizardry, 128, LIVING_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntityDecay.class, "decay", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, false);
        EntityRegistry.registerModEntity(EntityStormElemental.class, "storm_elemental", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntityEarthquake.class, "earthquake", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, false);
        EntityRegistry.registerModEntity(EntityIceLance.class, "ice_lance", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntityHailstorm.class, "hailstorm", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, false);
        EntityRegistry.registerModEntity(EntitySmokeBomb.class, "smoke_bomb", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntityEvilWizard.class, "evil_wizard", id++, wizardry, 128, LIVING_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntityDecoy.class, "decoy", id++, wizardry, 128, LIVING_UPDATE_INTERVAL, true);
        EntityRegistry.registerModEntity(EntityIceSpike.class, "ice_spike", id++, wizardry, 128, 1, true);
        // TODO: This should be a particle.
        EntityRegistry.registerModEntity(EntityLightningPulse.class, "lightning_pulse", id++, wizardry, 128, PROJECTILE_UPDATE_INTERVAL, false);

        EntityRegistry.registerEgg(EntityWizard.class, 0x19295e, 0xee9312);
        EntityRegistry.registerEgg(EntityEvilWizard.class, 0x290404, 0xee9312);
        
        // TODO: May need fixing
        List<Biome> biomes = new ArrayList<Biome>();
        for(Biome biome : Biome.EXPLORATION_BIOMES_LIST){
        	if(biome != null){
        		biomes.add(biome);
        	}
        }
        biomes.remove(Biomes.MUSHROOM_ISLAND);
        biomes.remove(Biomes.MUSHROOM_ISLAND_SHORE);
        // For reference: 5, 1, 1 are the parameters for the witch in vanilla.
        EntityRegistry.addSpawn(EntityEvilWizard.class, 3, 1, 1, EnumCreatureType.MONSTER, biomes.toArray(new Biome[biomes.size()]));

	}

	/** Called from the init method in the main mod class to register all the recipes. */
	public static void registerRecipes(){

        ItemStack magicCrystalStack = new ItemStack(WizardryItems.magic_crystal);
        ItemStack magicWandStack = new ItemStack(WizardryItems.magic_wand, 1, Tier.BASIC.maxCharge);
        ItemStack goldNuggetStack = new ItemStack(Items.GOLD_NUGGET);
        ItemStack stickStack = new ItemStack(Items.STICK);
        ItemStack bookStack = new ItemStack(Items.BOOK);
        ItemStack stringStack = new ItemStack(Items.STRING);
        ItemStack spellBookStack = new ItemStack(WizardryItems.spell_book, 1, Spells.magic_missile.id());
        ItemStack arcaneWorkbenchStack = new ItemStack(WizardryBlocks.arcane_workbench);
        ItemStack stoneStack = new ItemStack(Blocks.STONE);
        ItemStack lapisBlockStack = new ItemStack(Blocks.LAPIS_BLOCK);
        ItemStack purpleCarpetStack = new ItemStack(Blocks.CARPET, 1, 10);
        ItemStack wizardHandbookStack = new ItemStack(WizardryItems.wizard_handbook);
        ItemStack crystalFlowerStack = new ItemStack(WizardryBlocks.crystal_flower);
        ItemStack magicCrystalStack1 = new ItemStack(WizardryItems.magic_crystal, 2);
        ItemStack magicCrystalStack2 = new ItemStack(WizardryItems.magic_crystal, 9);
        ItemStack crystalBlockStack = new ItemStack(WizardryBlocks.crystal_block);
        ItemStack manaFlaskStack = new ItemStack(WizardryItems.mana_flask);
        ItemStack bottleStack = new ItemStack(Items.GLASS_BOTTLE);
        ItemStack gunpowderStack = new ItemStack(Items.GUNPOWDER);
        ItemStack blazePowderStack = new ItemStack(Items.BLAZE_POWDER);
        ItemStack spiderEyeStack = new ItemStack(Items.SPIDER_EYE);
        // Coal or charcoal is equally fine, hence the wildcard value
        ItemStack coalStack = new ItemStack(Items.COAL, 1, OreDictionary.WILDCARD_VALUE);
        ItemStack firebombStack = new ItemStack(WizardryItems.firebomb, 3);
        ItemStack poisonBombStack = new ItemStack(WizardryItems.poison_bomb, 3);
        ItemStack smokeBombStack = new ItemStack(WizardryItems.smoke_bomb, 3);
        ItemStack transportationStoneStack = new ItemStack(WizardryBlocks.transportation_stone, 2);
        ItemStack silkStack = new ItemStack(WizardryItems.magic_silk);
        ItemStack silkStack1 = new ItemStack(WizardryItems.magic_silk, 2);
        ItemStack hatStack = new ItemStack(WizardryItems.wizard_hat);
        ItemStack robeStack = new ItemStack(WizardryItems.wizard_robe);
        ItemStack leggingsStack = new ItemStack(WizardryItems.wizard_leggings);
        ItemStack bootsStack = new ItemStack(WizardryItems.wizard_boots);
        ItemStack scrollStack = new ItemStack(WizardryItems.blank_scroll);
        ItemStack paperStack = new ItemStack(Items.PAPER);
        
        GameRegistry.addRecipe(magicWandStack, "  x", " y ", "z  ", 'x', magicCrystalStack, 'y', stickStack, 'z', goldNuggetStack);
        GameRegistry.addRecipe(spellBookStack, " x ", "xyx", " x ", 'x', magicCrystalStack, 'y', bookStack);
        GameRegistry.addRecipe(arcaneWorkbenchStack, "vwv", "xyx", "zzz", 'v', goldNuggetStack, 'w', purpleCarpetStack, 'x', magicCrystalStack, 'y', lapisBlockStack, 'z', stoneStack);
        GameRegistry.addRecipe(manaFlaskStack, "yyy", "yxy", "yyy", 'x', bottleStack, 'y', magicCrystalStack);
        GameRegistry.addRecipe(transportationStoneStack, " x ", "xyx", " x ", 'x', stoneStack, 'y', magicCrystalStack);
        GameRegistry.addRecipe(hatStack, "yyy", "y y", 'y', silkStack);
        GameRegistry.addRecipe(robeStack, "y y", "yyy", "yyy", 'y', silkStack);
        GameRegistry.addRecipe(leggingsStack, "yyy", "y y", "y y", 'y', silkStack);
        GameRegistry.addRecipe(bootsStack, "y y", "y y", 'y', silkStack);
        GameRegistry.addRecipe(silkStack1, " x ", "xyx", " x ", 'x', stringStack, 'y', magicCrystalStack);
        GameRegistry.addRecipe(crystalBlockStack, "zzz", "zzz", "zzz", 'z', magicCrystalStack);

        GameRegistry.addShapelessRecipe(wizardHandbookStack, bookStack, magicCrystalStack);
        GameRegistry.addShapelessRecipe(magicCrystalStack1, crystalFlowerStack);
        GameRegistry.addShapelessRecipe(magicCrystalStack2, crystalBlockStack);
        if(Wizardry.settings.firebombIsCraftable) GameRegistry.addShapelessRecipe(firebombStack, bottleStack, gunpowderStack, blazePowderStack, blazePowderStack);
        if(Wizardry.settings.poisonBombIsCraftable) GameRegistry.addShapelessRecipe(poisonBombStack, bottleStack, gunpowderStack, spiderEyeStack, spiderEyeStack);
        if(Wizardry.settings.smokeBombIsCraftable) GameRegistry.addShapelessRecipe(smokeBombStack, bottleStack, gunpowderStack, coalStack, coalStack);
        if(Wizardry.settings.useAlternateScrollRecipe){
            GameRegistry.addShapelessRecipe(scrollStack, paperStack, stringStack, magicCrystalStack);
        }else{
            GameRegistry.addShapelessRecipe(scrollStack, paperStack, stringStack);
        }
        
        // Mana flask recipes
        ItemStack miscWandStack;
        
        for(Element element : Element.values()){
        	for(Tier tier : Tier.values()){
        		miscWandStack = new ItemStack(WizardryUtilities.getWand(tier, element), 1, OreDictionary.WILDCARD_VALUE);
        		GameRegistry.addShapelessRecipe(miscWandStack, miscWandStack, manaFlaskStack);
        	}
        }
        
        ItemStack miscArmourStack;
        
        for(Element element : Element.values()){
        	for(EntityEquipmentSlot slot : WizardryUtilities.ARMOUR_SLOTS){
	        	miscArmourStack = new ItemStack(WizardryUtilities.getArmour(element, slot), 1, OreDictionary.WILDCARD_VALUE);
	        	GameRegistry.addShapelessRecipe(miscArmourStack, miscArmourStack, manaFlaskStack);
        	}
        }
	}

}
