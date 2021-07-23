/*
 * Copyright (C) 2021 Jacob Wysko
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

package org.wysko.midis2jam2.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static java.awt.Cursor.getPredefinedCursor;

public abstract class Displays extends JFrame {
	
	public void display() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	/**
	 * Shows or hides the cursor.
	 *
	 * @param hide if true, hides the cursor, false shows the cursor
	 */
	@SuppressWarnings("java:S2301")
	public void hideCursor(boolean hide) {
		if (hide) {
			this.setCursor(this.getToolkit().createCustomCursor(
					new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
					new Point(),
					null));
		} else {
			this.setCursor(getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
}
