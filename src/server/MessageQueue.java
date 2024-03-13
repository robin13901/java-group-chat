package server;

public class MessageQueue {
    private static final int MAX_SIZE = 10;
    private final String[] messages = new String[MAX_SIZE];
    private int startIndex = 0;
    private int currentSize = 0;
    private int currentIndex = 0;

    public void addMessage(String message){
        messages[currentIndex] = message;
        currentIndex++;
        currentIndex = currentIndex % MAX_SIZE;
        currentSize = Math.min(currentSize + 1, MAX_SIZE);
        if(currentSize == MAX_SIZE){
            startIndex = (startIndex + 1) % MAX_SIZE;
        }
    }

    public String getMessage(int index){
        if(messages[index % MAX_SIZE] == null){
            return "";
        }
        return messages[index % MAX_SIZE];
    }

    public int getSize(){
        return currentSize;
    }

    public int getStartIndex(){
        return startIndex;
    }
}