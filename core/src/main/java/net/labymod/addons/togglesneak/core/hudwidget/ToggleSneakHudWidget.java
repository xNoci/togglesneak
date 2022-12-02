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

package net.labymod.addons.togglesneak.core.hudwidget;

import javax.inject.Inject;
import net.labymod.addons.togglesneak.core.controller.ToggleSneakController;
import net.labymod.addons.togglesneak.core.service.ToggleSneakService;
import net.labymod.api.client.entity.player.ClientPlayer;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.util.TextFormat;

public class ToggleSneakHudWidget extends TextHudWidget<TextHudWidgetConfig> {

  private final ToggleSneakController controller;
  private final ToggleSneakService service;

  private TextLine sprintingLine;
  private TextLine sneakingLine;

  private State lastSprintState;
  private State lastSneakState;

  @Inject
  private ToggleSneakHudWidget(ToggleSneakController contoller, ToggleSneakService service) {
    super("toggleSneak");

    this.controller = contoller;
    this.service = service;
  }

  @Override
  public void load(TextHudWidgetConfig config) {
    super.load(config);

    this.sprintingLine = super.createLine("Sprinting", "");
    this.updateSprintingTextLine(State.NO);

    this.sneakingLine = super.createLine("Sneaking", "");
    this.updateSneakingTextLine(State.NO);
  }

  @Override
  public boolean isVisibleInGame() {
    boolean sprinting = this.isSprinting();
    boolean sneaking = this.isSneaking();
    return sprinting || sneaking;
  }

  private boolean isSprinting() {
    State currentState = State.NO;
    ClientPlayer clientPlayer = this.labyAPI.minecraft().clientPlayer();
    if (clientPlayer != null && clientPlayer.isSprinting()) {
      if (this.service.isSprintToggled()) {
        currentState = State.TOGGLED;
      } else if (this.service.isSprintPressed()) {
        currentState = State.HOLDING;
      } else {
        currentState = State.VANILLA;
      }
    }

    this.updateSprintingTextLine(currentState);
    return currentState != State.NO;
  }

  private boolean isSneaking() {
    State currentState = State.NO;
    ClientPlayer clientPlayer = this.labyAPI.minecraft().clientPlayer();
    if (clientPlayer != null && clientPlayer.isCrouching()) {
      if (this.service.isSneakToggled()) {
        currentState = State.TOGGLED;
      } else if (this.service.isSneakPressed()) {
        currentState = State.HOLDING;
      } else {
        currentState = State.VANILLA;
      }
    }

    this.updateSneakingTextLine(currentState);
    return currentState != State.NO;
  }

  private void updateSprintingTextLine(State currentState) {
    if (this.lastSprintState == currentState) {
      return;
    }

    this.lastSprintState = currentState;
    this.sprintingLine.updateAndFlush(TextFormat.SNAKE_CASE.toUpperCamelCase(currentState.name()));
    this.sprintingLine.setVisible(currentState != State.NO);
  }

  private void updateSneakingTextLine(State currentState) {
    if (this.lastSneakState == currentState) {
      return;
    }

    this.lastSneakState = currentState;
    this.sneakingLine.updateAndFlush(TextFormat.SNAKE_CASE.toUpperCamelCase(currentState.name()));
    this.sneakingLine.setVisible(currentState != State.NO);
  }

  private enum State {
    VANILLA, HOLDING, TOGGLED, NO
  }
}
