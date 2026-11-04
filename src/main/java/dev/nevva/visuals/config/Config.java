package dev.nevva.visuals.config;

import java.util.LinkedHashMap;
import java.util.Map;

public class Config {
	public Map<String, Boolean> modules = new LinkedHashMap<>();
	public Map<String, Boolean> bools = new LinkedHashMap<>();
	public Map<String, Double> numbers = new LinkedHashMap<>();
	public Map<String, String> choices = new LinkedHashMap<>();
	public boolean hudBackground = true;
	public boolean notifications = true;
}
