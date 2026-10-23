package com.kamylo5.jarvisai

/** A single, explicitly allowed capability available to the assistant. */
data class AssistantTool(
    val name: String,
    val description: String,
    val execute: () -> String
)
