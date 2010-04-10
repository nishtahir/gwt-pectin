package com.pietschy.gwt.pectin.client.command;

import com.pietschy.gwt.pectin.client.channel.Channel;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Apr 1, 2010
 * Time: 4:11:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class AsyncEventSupport<R,E>
{
   private AsyncEventsImpl<R, E> alwaysEvents = new AsyncEventsImpl<R,E>();
   private AsyncEventsImpl<R, E> onNextCallEvents;

   public AsyncEvents<R, E> onNextCall()
   {
      if (onNextCallEvents == null)
      {
         onNextCallEvents = new AsyncEventsImpl();
      }
      return onNextCallEvents;
   }

   public AsyncEvents<R, E> always()
   {
      return alwaysEvents;
   }

   public Trigger prepareEvents()
   {
      Trigger trigger = new Trigger(alwaysEvents, onNextCallEvents);
      onNextCallEvents = null;
      return trigger;
   }

   protected Channel<R> getResultChannel()
   {
      return alwaysEvents.getResultChannel();
   }

   protected Channel<E> getErrorChannel()
   {
      return alwaysEvents.getErrorChannel();
   }

   public class Trigger
   {
      AsyncEventsImpl<R, E>[] events;

      public Trigger(AsyncEventsImpl<R, E>... events)
      {
         this.events = events;
      }

      public void fireStart()
      {
         for (AsyncEventsImpl<R, E> event : events)
         {
            if (event != null)
            {
               event.fireStart();
            }
         }
      }

      public void fireSuccess(R result)
      {
         for (AsyncEventsImpl<R, E> event : events)
         {
            if (event != null)
            {
               event.fireSuccess(result);
            }
         }
      }

      public void fireError(E error)
      {
         for (AsyncEventsImpl<R, E> event : events)
         {
            if (event != null)
            {
               event.fireError(error);
            }
         }
      }

      public void fireFinished()
      {
         for (AsyncEventsImpl<R, E> event : events)
         {
            if (event != null)
            {
               event.fireFinish();
            }
         }

      }
   }

}