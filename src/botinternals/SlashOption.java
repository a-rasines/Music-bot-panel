package botinternals;

import net.dv8tion.jda.api.interactions.commands.OptionType;

public class SlashOption {
	public String name;
	public String desc;
	public OptionType type;
	public boolean required;
	public SlashOption(String name, String desc, OptionType type, boolean required) {
		this.name = name;
		this.desc = desc;
		this.type = type;
		this.required = required;
	}

}
