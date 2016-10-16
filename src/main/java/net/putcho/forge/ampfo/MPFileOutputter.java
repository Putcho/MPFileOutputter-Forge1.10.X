package net.putcho.forge.ampfo;

import static net.putcho.forge.ampfo.MPFileOutputter.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

@Mod(modid=MODID, version=VERSION)
public class MPFileOutputter {
	public static final String MODID = "mpfileoutputter";
	public static final String VERSION = "1.0";

	public static final String FORMAT = "%s:%s:%s:%d=";

	private static final String ln = System.getProperty("line.separator");

	@EventHandler
	public void postInit(FMLPostInitializationEvent e){
		List<ModContainer> mods = Loader.instance().getActiveModList();
		for(ModContainer mod: mods){
			String modid = mod.getModId();
			List<String> blocks = new ArrayList<String>();
			List<String> items = new ArrayList<String>();
			for(ResourceLocation loc: Item.REGISTRY.getKeys()){
				if(loc.getResourceDomain().equals(modid)){
					Item item = Item.REGISTRY.getObject(loc);
					String id = loc.getResourcePath();
					int meta = 0;
					if(item instanceof ItemBlock){
						blocks.add(String.format(FORMAT, "block", modid, id, meta));
					}else{
						items.add(String.format(FORMAT, "item", modid, id, meta));
					}
				}
			}
			Collections.sort(blocks);
			Collections.sort(items);

			if(!blocks.isEmpty() || !items.isEmpty()){
				StringBuilder sb = new StringBuilder();
				sb.append(String.format("#%s", mod.getName()));
				sb.append(ln);
				if(!blocks.isEmpty()){
					sb.append(ln);
					sb.append("#Block");
					sb.append(ln);
					for(String line: blocks){
						sb.append(line);
						sb.append(ln);
					}
				}

				if(!items.isEmpty()){
					sb.append(ln);
					sb.append("#Item");
					sb.append(ln);
					for(String line: items){
						sb.append(line);
						sb.append(ln);
					}
				}

				output(sb, modid);
			}
		}
	}

	private void output(StringBuilder sb, String modid){
		try{
			File mpfile = new File(Minecraft.getMinecraft().mcDataDir, String.format("mods/%s.mp", modid));
			mpfile.createNewFile();
			FileOutputStream fos = new FileOutputStream(mpfile);
			PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(fos, "UTF-8")));
			pw.write(sb.toString());
			pw.close();
			fos.close();

			System.out.println(String.format("[MPFileOutputter]Output success of %s", modid));
		}catch(Exception e){
			e.printStackTrace();
			System.out.println(String.format("[MPFileOutputter]Output error of %s", modid));
		}
	}
}
