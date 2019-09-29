import core.GameObject;
import core.Position;
import core.utils.Random;

public class Player extends GameObject {
    int id = 0;
    String name = "Безымянный Скиталец";
    int HP = 100;
    int energy  = 200;
    int fieldOfView = 10;
    int level = 1;
    int xp = 2;

    String state;

    char mapIcon = '@';

    public String[] welcomeMessages = {" прибыл из космоса", " вылез из под земли", " наконец-то вышел из дома",
            " был добавлен в мир"};

    public Player(String name, int id){
        super("name", new Position(0,0), 200, '@');
        this.name = name;
        this.id = id;
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
        super.position.y += deltaY;
        System.out.println(name + ": Моя позиция: " + position.x + ", " + position.y);
        energy -= Math.abs(deltaY);
    }

    public void sleep() {
        if(energy <= 50) {
            System.out.println(name + ": Z-z-z-z...");
            energy += 100;
        } else {
            System.out.println(name + ": Я пока не устал!");
        }
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

        if (commandArray[0].toLowerCase().equals("идти")){
            System.out.println(commandArray[1].toLowerCase());
            if(commandArray[1].toLowerCase().equals("вправо")){
                moveX(Integer.parseInt(commandArray[2]));
            } else if (commandArray[1].toLowerCase().equals("влево")){
                moveX(Integer.parseInt(commandArray[2]) * -1);
            } else if (commandArray[1].toLowerCase().equals("вниз")){
                moveY(Integer.parseInt(commandArray[2]));
            } else if (commandArray[1].toLowerCase().equals("вверх")){
                System.out.println("Вверх");
                moveY(Integer.parseInt(commandArray[2]) * -1);
            }
            answer = "Моя позиция: x=" + Integer.toString(position.x) + ", y=" + Integer.toString(position.y);
        } else if (commandArray[0].toLowerCase().equals("осмотреть")){
            if (commandArray[1].toLowerCase().equals("себя")){
                answer = "Меня зовут: " + name + "\n" +
                        "HP: " + HP + "\n" +
                        "Энергия: " + energy + "\n" +
                        "Радиус обзора: " + fieldOfView;
            }
        } else {
            answer = name + randUnknownCommandPhrases[Random.randInt(0, randUnknownCommandPhrases.length - 1)];
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
    public int getPosX() {
        return position.x;
    }
    public int getPosY() {
        return position.y;
    }

}
