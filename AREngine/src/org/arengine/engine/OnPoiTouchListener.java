/**
 *
 *Copyright (C) 2012 Atef Haouari - VEEUP
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU Lesser General Public
 *License as published by the Free Software Foundation; either
 *version 3 of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *Lesser General Public License for more details.
 *
 *You should have received a copy of the GNU Lesser General Public
 *License along with this program; if not, write to the Free Software
 *Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA* 
 */
package org.arengine.engine;

/**
 * The listener interface for receiving onPoiTouch events.
 * The class that is interested in processing a onPoiTouch
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addOnPoiTouchListener<code> method. When
 * the onPoiTouch event occurs, that object's appropriate
 * method is invoked.
 *
 * @see OnPoiTouchEvent
 */
public interface OnPoiTouchListener {

	/**
	 * On touch.
	 *
	 * @param poi the poi
	 */
	void onTouch(Poi poi);
}
