package com.github.axodenelcorvus.commands;

public interface HelpUsageDisplay {
    /**
     * Returns example format of usage of given subcommand or the application itself.
     * @return String
     */
    String getUsageExample();

    /**
     * Returns description of a given subcommand
     * @return String
     */
    String getCommandDescription();

    /**
     * Returns possible options and their roles for a given subcommand. All options are expected to be long flagged.
     * @return String[] - Options where each even index represents a single option's usage format
     * and odd index represents that option's brief description. Empty array returned
     * if no possible options exist
     */
    String[] getOptionsList();

    /**
     * Returns required arguments alongside a description of their
     * function and/or example case of use for a given subcommand.
     * @return String[] - Required arguments where each index represents a required
     * argument and odd indexes represent function description and/or example
     * case of use. Empty array returned if no arguments are required for subcommand.
     */
    String[] getRequiredArguments();

}
