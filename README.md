# quadwitchseed
## original code showcases:
  * L64's fast quad hut finding: https://www.youtube.com/watch?v=97OdqeiUfHw&t
  * CodeRaider's seedfinder: https://www.youtube.com/watch?v=7-BF0kez8fE
    * actualy used the improved version by bbinme2005

## What is this repository about?
  This is tool which you can use to find quad witch huts really fast when specified least amount of spawning spaces.
### What did I used to achieve this?
For the base, I used L64's library/seedfinder which had a really nice ability to get multiple quadhut seeds from one found configuration to make this process faster. But it was also finding only witch huts and ignored the fact, that witches spawn anywhere in swamp where a structure is. It also wasn't using multiple cores. CodeRaider's seed finder was outdated and slow, but had IntCache modified for concurrent use already, so I could use it. It was also able to calculate several useful things as total spawnable area, maximal distance to center etc. So I modified L64's code and copied some functionality from CodeRaider's one.

I hope authors of original code don't mind me publishing this.

### How to use
The best way to use this is opening this as a NetBeans project as it is. You can create new project using these source files in other IDEs as well, or just compile it via javac command. I recomend you configuring the constants **SeedFinder.BATCHSIZE** (number of seeds checked by one thread) and **SeedFinder.THREADCOUNT** as how you want to run it and then launch class SeedFinder.
