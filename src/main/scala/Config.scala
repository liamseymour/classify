object Config {
	var config_path = makeConfigPath()

	private def makeConfigPath(): String = {
		"/home/liam/.config/classify/config.json"
	}
}
