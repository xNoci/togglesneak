/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.labymod.addons.togglesneak.core.controller;

import net.labymod.api.Laby;
import net.labymod.api.client.options.MinecraftInputMapping;
import net.labymod.api.client.options.ToggleInputMapping;
import net.labymod.api.event.Phase;
import net.labymod.api.reference.annotation.Referenceable;
import org.jetbrains.annotations.Nullable;

@Nullable
@Referenceable
public abstract class ToggleSneakController {

  public abstract Phase phase();

  public void test() {
    // ignored
  }

  public boolean isSprintPressed() {
    return this.sprintInputMapping().isActuallyDown();
  }

  public boolean isSneakPressed() {
    return this.sneakInputMapping().isActuallyDown();
  }

  public void setSprinting(boolean sprinting) {
    this.update(this.sprintInputMapping(), sprinting);
  }

  public void setSneaking(boolean sneaking) {
    this.update(this.sneakInputMapping(), sneaking);
  }

  private MinecraftInputMapping sprintInputMapping() {
    return Laby.labyAPI().minecraft().options().sprintInput();
  }

  private MinecraftInputMapping sneakInputMapping() {
    return Laby.labyAPI().minecraft().options().sneakInput();
  }

  private void update(MinecraftInputMapping inputMapping, boolean press) {
    if (!(inputMapping instanceof ToggleInputMapping)) {
      if (press == inputMapping.isDown()) {
        return;
      }

      if (press) {
        inputMapping.press();
      } else {
        inputMapping.unpress();
      }

      return;
    }

    ToggleInputMapping toggleInputMapping = (ToggleInputMapping) inputMapping;
    if (press) {
      toggleInputMapping.forcePress();
    } else {
      toggleInputMapping.forceUnpress();
    }
  }
}
