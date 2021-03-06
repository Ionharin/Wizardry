package electroblob.wizardry.item;

import java.util.List;

import electroblob.wizardry.entity.projectile.EntityFirebomb;
import electroblob.wizardry.registry.WizardryTabs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemFirebomb extends Item {

    public ItemFirebomb(){
        this.maxStackSize = 16;
        this.setCreativeTab(WizardryTabs.WIZARDRY);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand){
    
        if(!player.capabilities.isCreativeMode){
            --stack.stackSize;
        }

        player.playSound(SoundEvents.ENTITY_SNOWBALL_THROW, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if(!world.isRemote){
        	EntityFirebomb firebomb = new EntityFirebomb(world, player);
            // This is the standard set of parameters for this method, used by snowballs and ender pearls.
        	firebomb.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, 0.0f, 1.5f, 1.0f);
            world.spawnEntityInWorld(firebomb);
        }

        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item parItem, CreativeTabs parTab, List<ItemStack> parListSubItems){
        parListSubItems.add(new ItemStack(this, 1));
    }
}