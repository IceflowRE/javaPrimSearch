
public class ConsoleProgressBar {
	
	private boolean swap;
	
	public ConsoleProgressBar() {
		this.swap = false;
	}
	
	public String getProgress(int percent) {
		StringBuilder sBuilder = new StringBuilder("");
		StringBuilder sBuilderSpace = new StringBuilder("");
		if ((percent < 0) || (percent > 100)) {
			this.swap = !this.swap;
			if (swap) {
				return "\r                    = =  = =                    ";
			} else {
				return "\r                     = == =                     ";
			}
		}
		
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < (percent / 2); i++) {
			sBuilder.append("=");
		}
		for (int i = 0; i < (51 - (percent / 2)); i++) {
			sBuilderSpace.append(" ");
		}
		result.append("\r");
		result.append(sBuilder.toString());
		result.append(sBuilderSpace.toString());
		result.append(percent + "%");
		return result.toString();
	}
}
