/*
 * Copyright (C) 2023 Jacob Wysko
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package org.wysko.midis2jam2.gui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import org.wysko.midis2jam2.gui.components.Linkable
import org.wysko.midis2jam2.gui.components.Midis2jam2Logo
import org.wysko.midis2jam2.gui.components.TextWithLink
import org.wysko.midis2jam2.gui.loadDependencies
import org.wysko.midis2jam2.gui.util.openHelp
import org.wysko.midis2jam2.gui.viewmodel.I18n
import org.wysko.midis2jam2.util.Utils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen() {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton({
                openHelp()
            }) {
                Icon(painterResource("/ico/help.svg"), contentDescription = "Help")
            }
        }
    ) {
        LazyColumn(
            Modifier.padding(horizontal = 16.dp),
        ) {
            item {
                Spacer(Modifier.height(16.dp))
                Midis2jam2Logo()
                Text(I18n["midis2jam2_description"].value, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
                Text("Copyright © MMXXIII Jacob Wysko • ${Utils.resourceToString("/version.txt")}")
                Text("This program comes with absolutely no warranty.")
                TextWithLink(
                    text = "See the GNU General Public License for more details.",
                    textToLink = "GNU General Public License",
                    link = Linkable.URL("https://www.gnu.org/licenses/gpl-3.0.en.html")
                )
                Spacer(Modifier.height(16.dp))
                ProvideTextStyle(MaterialTheme.typography.bodySmall) {
                    Text("Some assets © 2007 Scott Haag. All rights reserved. Used with permission.")
                    Text("SoundFont is a registered trademark of E-mu Systems, Inc.")
                    Spacer(Modifier.height(16.dp))
                    Text("Development contributors:", fontStyle = FontStyle.Italic)
                    Text("Julian Lachniet")
                    Text("JonnyCrash420")
                    Text("nikitalita")
                    Text("rxuglr")
                    Text("vgking1")
                    Spacer(Modifier.height(16.dp))
                    Text("Attributions and contributions:", fontStyle = FontStyle.Italic)
                    Text("\"Guitar\" by vishnevsky.yaroslav licensed under CC BY 4.0, modified.")
                    Text("\"tabr\" by Matthew Leonawicz licensed under MIT.")
                    Text("Clarinet texture by Mr. Tremolo Measure.")
                    Text("Tinkle Bell model by TheCococQuartz.")
                    Spacer(Modifier.height(16.dp))
                    Text("Internationalization contributions:", fontStyle = FontStyle.Italic)
                    Text("中文: otomad")
                    Text("Suomi: Dermoker")
                    Text("Français: Jacob Wysko")
                    Text("Norsk: Trygve Larsen")
                    Text("Pусский: Rxuglr")
                    Text("Español: Mr. Tremolo Measure")
                    Text("Tagalog: GlovePerson")
                    Text("ไทย: Hariwong Lonarai")
                    Spacer(Modifier.height(16.dp))
                    Text("Open-source software licenses:", fontStyle = FontStyle.Italic)
                    loadDependencies().forEach { (dependencyName, _, licenses) ->
                        if (licenses.first().url != null) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = dependencyName
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("•")
                                Spacer(Modifier.width(8.dp))
                                TextWithLink(
                                    text = licenses.first().name,
                                    textToLink = licenses.first().name,
                                    link = Linkable.URL(licenses.first().url ?: return@Row),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}