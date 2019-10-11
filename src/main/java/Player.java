import core.GameObject;
import core.Position;
import core.utils.Random;

public class Player extends GameObject {
    int id;
    String name;

    Position oldPos;

    int HP = 100;
    int fieldOfView = 10;
    int level = 1;
    int xp = 2;

    String state;

    static final char[] ICONS = {
            '☃',
            '☠',
            '☭'
    };

    char mapIcon = '@';

    private String[] welcomeMessages = {" прибыл из космоса", " вылез из под земли", " наконец-то вышел из дома",
            " был добавлен в мир"};

    Player(String name, Position pos, int id){
        super("name", pos, 200, '@');
        oldPos = pos;
        this.name = name;
        this.id = id;
        mapIcon = ICONS[Random.randInt(0, ICONS.length - 1)];
        changeEnergy(100);
        System.out.println(name + welcomeMessages[(int)(Math.random() * ((welcomeMessages.length)))]);
    }

    private void moveX(int deltaX) {
        int dangerChance = 10;
        if(deltaX > fieldOfView) {
            dangerChance = 75;
        }
        int randInt = Random.randInt(0,100);
        if(randInt<=dangerChance){
            int looseHP = Random.randInt(5, 30);
            System.out.println(name + ": так быстро бежал, что по дороге не заметил яму, споткнулся и потерял " +
                    randInt +" HP");
            HP -= looseHP;
        }
        oldPos = new Position(position.x, position.y);
        position.x += deltaX;
        System.out.println(name + ": Моя позиция: " + position.x + ", " + position.y);
        changeEnergy(Math.abs(deltaX) * -1);
    }
    private void moveY(int deltaY) {
        int dangerChanse = 10;
        if(deltaY > fieldOfView) {
            dangerChanse = 75;
        }
        int randInt = Random.randInt(0, 100);
        if(randInt<=dangerChanse){
            int looseHP = Random.randInt(5, 30);
            System.out.println(name + ": так быстро бежал, что по дороге не заметил яму, споткнулся и потерял " +
                    randInt +" HP");
            HP -= looseHP;
        }
        oldPos = new Position(position.x, position.y);
        position.y += deltaY;
        System.out.println(name + ": Моя позиция: " + position.x + ", " + position.y);
        changeEnergy(Math.abs(deltaY) * -1);
    }

    void teleportPlayer(Position newPosition){
        position.x = newPosition.x;
        position.y = newPosition.y;
    }


    String executeCommand(String command) {

        String answer = "";
        String[] commandArray = command.split(" ");

        String[] randUnknownCommandPhrases = {": Оу, ума не приложу как это сделать, но я могу вот что: \n",
                ": Вобще без понятия как это сделать :( Но мы можем попробовать: \n",
                ": Я думаю, я должен научиться делать это, но пока я могу только: \n"};

        String[] commands = {"идти (вверх | вниз | вправо | влево) (n) -  \n" +
                "Идти в определенном направлении n шагов\n",
                "осмотреть (местность | себя) - увидеть карту в поле вашего зрения/узнать статы своего персонажа\n",
                "спать - восстановить энергию, если ее мало"};

        if (commandArray[0].toLowerCase().equals("идти")) {
            if (getEnergy() >= Integer.parseInt(commandArray[2])) {
                if (commandArray[1].toLowerCase().equals("вправо")) {
                    moveY(Integer.parseInt(commandArray[2]));
                } else if (commandArray[1].toLowerCase().equals("влево")) {
                    moveY(Integer.parseInt(commandArray[2]) * -1);
                } else if (commandArray[1].toLowerCase().equals("вниз")) {
                    moveX(Integer.parseInt(commandArray[2]));
                } else if (commandArray[1].toLowerCase().equals("вверх")) {
                    moveX(Integer.parseInt(commandArray[2]) * -1);
                }
                answer = ": Моя позиция: x=" + position.x + ", y=" + position.y;
            } else {
                answer = ": Что-то мне подсказывает, что мне не хватит сил добраться так далеко...";
            }
        } else if (commandArray[0].toLowerCase().equals("осмотреть")){
            if (commandArray[1].toLowerCase().equals("себя")){
                answer = ": Меня зовут: " + name + "\n" +
                        "HP: " + HP + "\n" +
                        "Энергия: " + getEnergy() + "\n" +
                        "Радиус обзора: " + fieldOfView;
            } else {
                answer = ": Меня окружает только тьма...";
            }
        } else if (commandArray[0].toLowerCase().equals("спать")){
            if(getEnergy() <= 50) {
                changeEnergy(99);
                answer =  ": Z-z-z-z...";
            } else {
                answer = ": Я пока не устал!";
            }
        } else if (commandArray[0].toLowerCase().equals("тп")){
            teleportPlayer(new Position(Integer.parseInt(commandArray[1]), Integer.parseInt(commandArray[2])));
            answer = ": Вы заюзали дебаг функцию, админы уже заметили это, за вами выехал магический спецназ!";
        } else {
            answer = randUnknownCommandPhrases[Random.randInt(0, randUnknownCommandPhrases.length - 1)];
            for (String item: commands) {
                answer += item;
            }
        }
        return answer;
    }

    public int getXP(){
        return xp;
    }
    public int getLevel(){
        return level;
    }
    public String getName() {
        return name;
    }
    public int getHP() {
        return HP;
    }
    public int getFOV() {
        return fieldOfView;
    }
    public Position getPos() {
        return position;
    }
}
