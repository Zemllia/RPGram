import core.GameObject;
import core.Position;
import core.utils.Random;

public class Player extends GameObject {
    int id = 0;
    String name = "Безымянный Скиталец";

    Position oldPos = new Position(0,0);

    int HP = 100;
    int energy  = 20000;
    int fieldOfView = 10;
    int level = 1;
    int xp = 2;

    String state;

    char mapIcon = '@';

    public String[] welcomeMessages = {" прибыл из космоса", " вылез из под земли", " наконец-то вышел из дома",
            " был добавлен в мир"};

    public Player(String name, Position pos, int id){
        super("name", pos, 200, '@');
        oldPos = pos;
        this.name = name;
        this.id = id;
        mapIcon = name.charAt(0);
        System.out.println(name + welcomeMessages[(int)(Math.random() * ((welcomeMessages.length)))]);
    }

    public void moveX(int deltaX) {
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
        energy -= Math.abs(deltaX);
    }
    public void moveY(int deltaY) {
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
        energy -= Math.abs(deltaY);
    }


    public String executeCommand(String command) {

        System.out.println(command);

        String answer = "";
        String[] commandArray = command.split(" ");

        String[] randUnknownCommandPhrases = {": хм, ума не приложу как это сделать, но я могу вот что: \n",
                ": я так не умею :( Кстати, могу так: \n",
                ": возможно мне стоит научиться делать это, но пока я могу только: \n"};

        String[] commands = {"идти <вверх/вниз/вправо/влево> <n> - идти в любую сторону n шагов \n",
                "осмотреть <местность/себя> - увидеть карту в поле вашего зрения/узнать статы своего персонажа\n",
                "спать - восстановить энергию, если ее мало"};

        if (commandArray[0].toLowerCase().equals("идти")) {
            System.out.println(commandArray[1].toLowerCase());
            if (energy >= Integer.parseInt(commandArray[2])) {
                if (commandArray[1].toLowerCase().equals("вправо")) {
                    moveY(Integer.parseInt(commandArray[2]));
                } else if (commandArray[1].toLowerCase().equals("влево")) {
                    moveY(Integer.parseInt(commandArray[2]) * -1);
                } else if (commandArray[1].toLowerCase().equals("вниз")) {
                    moveX(Integer.parseInt(commandArray[2]));
                } else if (commandArray[1].toLowerCase().equals("вверх")) {
                    moveX(Integer.parseInt(commandArray[2]) * -1);
                }
                answer = ": Моя позиция: x=" + Integer.toString(position.y) + ", y=" + Integer.toString(position.x);
            } else {
                answer = ": Что-то мне подсказывает, что мне не хватит сил добраться так далеко...";
            }
        } else if (commandArray[0].toLowerCase().equals("осмотреть")){
            if (commandArray[1].toLowerCase().equals("себя")){
                answer = ": Меня зовут: " + name + "\n" +
                        "HP: " + HP + "\n" +
                        "Энергия: " + energy + "\n" +
                        "Радиус обзора \u26BD: " + fieldOfView;
            } else {
                answer = ": Меня окружает только тьма...";
            }
        } else if (commandArray[0].toLowerCase().equals("спать")){
            if(energy <= 50) {
                energy += 99;
                answer =  ": Z-z-z-z...";
            } else {
                answer = ": Я пока не устал!";
            }
        } else {
            answer = randUnknownCommandPhrases[Random.randInt(0, randUnknownCommandPhrases.length - 1)];
            for (String item: commands) {
                answer = answer + item;
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
    public int getEnergy() {
        return energy;
    }
    public int getFOV() {
        return fieldOfView;
    }
    public Position getPos() {
        return position;
    }
}
