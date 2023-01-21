package devarea.fr.discord.commands;

import com.mongodb.lang.Nullable;

public class Context {

    protected final String messageId;
    protected final String channelId;

    protected Context(final String messageId, final String channelId) {
        this.messageId = messageId;
        this.channelId = channelId;
    }

    public boolean match(Context context) {

        if (this.messageId != null && context.messageId != null && !this.messageId.equals(context.messageId))
            return false;
        if (this.channelId != null && context.channelId != null && !this.channelId.equals(context.channelId))
            return false;

        return true;
    }

    public static ContextBuilder builder() {
        return new ContextBuilder();
    }

    public static class ContextBuilder {

        protected String messageId;
        protected String channelId;

        private ContextBuilder(){

        }

        public ContextBuilder messageId(final String messageId) {
            this.messageId = messageId;
            return this;
        }

        public ContextBuilder channelId(final String channelId) {
            this.channelId = channelId;
            return this;
        }

        public Context build() {
            return new Context(messageId, channelId);
        }

    }
}
