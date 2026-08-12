package net.artmaster.xaero_colonies.utils;

import net.artmaster.xaero_colonies.ModMain;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@EventBusSubscriber(modid = ModMain.MODID)
public class ServerScheduler {

    private static final List<ScheduledTask> TASKS = new ArrayList<>();

    public static void schedule(int delayTicks, Runnable action) {
        TASKS.add(new ScheduledTask(delayTicks, action));
    }

    public static void tick() {
        Iterator<ScheduledTask> it = TASKS.iterator();
        while (it.hasNext()) {
            ScheduledTask task = it.next();
            task.ticksLeft--;

            if (task.ticksLeft <= 0) {
                task.action.run();
                it.remove();
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        ServerScheduler.tick();
    }

    private static class ScheduledTask {
        int ticksLeft;
        Runnable action;

        ScheduledTask(int ticksLeft, Runnable action) {
            this.ticksLeft = ticksLeft;
            this.action = action;
        }
    }
}