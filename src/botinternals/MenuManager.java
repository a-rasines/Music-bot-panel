package botinternals;

import java.util.List;

import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

public class MenuManager {
	private StringSelectMenu.Builder menu;
	private String id;
	private MenuAction mt;
	private List<SelectOption> options;
	
	public MenuManager(String id, MenuAction mt) {
		this.mt = mt;
		this.id = id;
		menu = StringSelectMenu.create(id);
		EventHandler.selectionMenu.put(id, this);
	}
	
	public void addOption(String name, String value) {
		menu.addOption(name.length()>25?name.substring(0, 24):name, value);
	}
	
	public String returnAndDestroy(int pos) {
		EventHandler.selectionMenu.remove(id);
		return options.get(pos).getValue();
	}
	public String returnAndDestroy(SelectOption opt) { //Al faltar el .equals de SelectOption no puedo comprobar que opt pertenece a options
		EventHandler.selectionMenu.remove(id);
		return opt.getValue();
	}

	public interface MenuAction{
		public void execute(String value);
	}
	
	public void setMenuAction(MenuAction mt) {
		this.mt = mt;
	}
	
	public void execute(String value) {
		this.mt.execute(value);
	}
	
	public SelectMenu build() {
		options = menu.getOptions();
		return menu.build();
	}
	public List<SelectOption> getOptions(){
		return options;
	}

}
