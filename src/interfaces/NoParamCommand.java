package interfaces;

import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public interface NoParamCommand extends Command {
	public default OptionData[] params() {
		return new OptionData[0];
	};

}
