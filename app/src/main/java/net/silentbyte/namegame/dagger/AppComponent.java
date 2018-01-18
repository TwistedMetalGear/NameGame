package net.silentbyte.namegame.dagger;

import net.silentbyte.namegame.game.NameGameFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface AppComponent {

    void inject(NameGameFragment target);
}
