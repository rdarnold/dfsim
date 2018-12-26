package dfsim;

import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;

import dfsim.*;
import dfsim.gui.*;

public enum Trait {
    Accessible,
    Active,
    Adaptable,
    Admirable,
    Adventurous,
    Agreeable,
    Alert,
    Allocentric,
    Amiable,
    Anticipative,
    Appreciative,
    Articulate,
    Aspiring,
    Athletic,
    Attractive,
    Balanced,
    Benevolent,
    Brilliant,
    Calm,
    Capable,
    Captivating,
    Caring,
    Challenging,
    Charismatic,
    Charming,
    Cheerful,
    Clean,
    ClearHeaded,
    Clever,
    Colorful,
    Companionly,
    Compassionate,
    Conciliatory,
    Confident,
    Conscientious,
    Considerate,
    Constant,
    Contemplative,
    Cooperative,
    Courageous,
    Courteous,
    Creative,
    Cultured,
    Curious,
    Daring,
    Debonair,
    Decent,
    Decisive,
    Dedicated,
    Deep,
    Dignified,
    Directed,
    Disciplined,
    Discreet,
    Dramatic,
    Dutiful,
    Dynamic,
    Earnest,
    Ebullient,
    Educated,
    Efficient,
    Elegant,
    Eloquent,
    Empathetic,
    Energetic,
    Enthusiastic,
    Esthetic,
    Exciting,
    Extraordinary,
    Fair,
    Faithful,
    Farsighted,
    Felicific,
    Firm,
    Flexible,
    Focused,
    Forceful,
    Forgiving,
    Forthright,
    Freethinking,
    Friendly,
    FunLoving,
    Gallant,
    Generous,
    Gentle,
    Genuine,
    GoodNatured,
    Gracious,
    Hardworking,
    Healthy,
    Hearty,
    Helpful,
    Heroic,
    HighMinded,
    Honest,
    Honorable,
    Humble,
    Humorous,
    Idealistic,
    Imaginative,
    Impressive,
    Incisive,
    Incorruptible,
    Independent,
    Individualistic,
    Innovative,
    Inoffensive,
    Insightful,
    Insouciant,
    Intelligent,
    Intuitive,
    Invulnerable,
    Kind,
    Knowledge,
    Leaderly,
    Leisurely,
    Liberal,
    Logical,
    Lovable,
    Loyal,
    Lyrical,
    Magnanimous,
    ManySided,
    Masculine,
    Mature,
    Methodical,
    Meticulous,
    Moderate,
    Modest,
    MultiLeveled,
    Neat,
    Nonauthoritarian,
    Objective,
    Observant,
    Open,
    Optimistic,
    Orderly,
    Organized,
    Original,
    Painstaking,
    Passionate,
    Patient,
    Patriotic,
    Peaceful,
    Perceptive,
    Perfectionist,
    Personable,
    Persuasive,
    Planful,
    Playful,
    Polished,
    Popular,
    Practical,
    Precise,
    Principled,
    Profound,
    Protean,
    Protective,
    Providential,
    Prudent,
    Punctual,
    Pruposeful,
    Rational,
    Realistic,
    Reflective,
    Relaxed,
    Reliable,
    Resourceful,
    Respectful,
    Responsible,
    Responsive,
    Reverential,
    Romantic,
    Rustic,
    Sage,
    Sane,
    Scholarly,
    Scrupulous,
    Secure,
    Selfless,
    SelfCritical,
    SelfDefacing,
    SelfDenying,
    SelfReliant,
    SelfSufficent,
    Sensitive,
    Sentimental,
    Seraphic,
    Serious,
    Sexy,
    Sharing,
    Shrewd,
    Simple,
    Skillful,
    Sober,
    Sociable,
    Solid,
    Sophisticated,
    Spontaneous,
    Sporting,
    Stable,
    Steadfast,
    Steady,
    Stoic,
    Strong,
    Studious,
    Suave,
    Subtle,
    Sweet,
    Sympathetic,
    Systematic,
    Tasteful,
    Teacherly,
    Thorough,
    Tidy,
    Tolerant,
    Tractable,
    Trusting,
    Uncomplaining,
    Understanding,
    Undogmatic,
    Unfoolable,
    Upright,
    Urbane,
    Venturesome,
    Vivacious,
    Warm,
    WellBred,
    WellRead,
    WellRounded,
    Winning,
    Wise,
    Witty,
    Youthful,

    // Now, neutral traits
    Absentminded,
    Aggressive,
    Ambitious,
    Amusing,
    Artful,
    Ascetic,
    Authoritarian,
    BigThinking,
    Boyish,
    Breezy,
    Businesslike,
    Busy,
    Casual,
    Crebral,
    Chummy,
    Circumspect,
    Competitive,
    Complex,
    Confidential,
    Conservative,
    Contradictory,
    Crisp,
    Cute,
    Deceptive,
    Determined,
    Dominating,
    Dreamy,
    Driving,
    Droll,
    Dry,
    Earthy,
    Effeminate,
    Emotional,
    Enigmatic,
    Experimental,
    Familial,
    Folksy,
    Formal,
    Freewheeling,
    Frugal,
    Glamorous,
    Guileless,
    HighSpirited,
    Hurried,
    Hypnotic,
    Iconoclastic,
    Idiosyncratic,
    Impassive,
    Impersonal,
    Impressionable,
    Intense,
    Invisible,
    Irreligious,
    Irreverent,
    Maternal,
    Mellow,
    Modern,
    Moralistic,
    Mystical,
    Neutral,
    Noncommittal,
    Noncompetitive,
    Obedient,
    OldFashioned,
    Ordinary,
    Outspoken,
    Paternalistic,
    Physical,
    Placid,
    Political,
    Predictable,
    Preoccupied,
    Private,
    Progressive,
    Proud,
    Pure,
    Questioning,
    Quiet,
    Religious,
    Reserved,
    Restrained,
    Retiring,
    Sarcastic,
    SelfConscious,
    Sensual,
    Skeptical,
    Smooth,
    Soft,
    Solemn,
    Solitary,
    Stern,
    Stolid,
    Strict,
    Stubborn,
    Stylish,
    Subjective,
    Surprising,
    Tough,
    Unaggressive,
    Unambitious,
    Unceremonious,
    Unchanging,
    Undemanding,
    Unfathomable,
    Unhurried,
    Uninhibited,
    Unpatriotic,
    Unpredictable,
    Unreligious,
    Unsentimental,
    Whimsical,

    // Now, negative traits
    Abrasive,
    Abrupt,
    Agonizing,
    Aimless,
    Airy,
    Aloof,
    Amoral,
    Angry,
    Anxious,
    Apathetic,
    Arbitrary,
    Argumentative,
    Arrogantt,
    Artificial,
    Asocial,
    Assertive,
    Astigmatic,
    Barbaric,
    Bewildered,
    Bizarre,
    Bland,
    Blunt,
    Boisterous,
    Brittle,
    Brutal,
    Calculating,
    Callous,
    Cantakerous,
    Careless,
    Cautious,
    Charmless,
    Childish,
    Clumsy,
    Coarse,
    Cold,
    Colorless,
    Complacent,
    Complaintive,
    Compulsive,
    Conceited,
    Condemnatory,
    Conformist,
    Confused,
    Contemptible,
    Conventional,
    Cowardly,
    Crafty,
    Crass,
    Crazy,
    Criminal,
    Critical,
    Crude,
    Cruel,
    Cynical,
    Decadent,
    Deceitful,
    Delicate,
    Demanding,
    Dependent,
    Desperate,
    Destructive,
    Devious,
    Difficult,
    Dirty,
    Disconcerting,
    Discontented,
    Discouraging,
    Discourteous,
    Dishonest,
    Disloyal,
    Disobedient,
    Disorderly,
    Disorganized,
    Disputatious,
    Disrespectful,
    Disruptive,
    Dissolute,
    Dissonant,
    Distractible,
    Disturbing,
    Dogmatic,
    Domineering,
    Dull,
    EasilyDiscouraged,
    Egocentric,
    Enervated,
    Envious,
    Erratic,
    Escapist,
    Excitable,
    Expedient,
    Extravagant,
    Extreme,
    Faithless,
    False,
    Fanatical,
    Fanciful,
    Fatalistic,
    Fawning,
    Fearful,
    Fickle,
    Fiery,
    Fixed,
    Flamboyant,
    Foolish,
    Forgetful,
    Fraudulent,
    Frightening,
    Frivolous,
    Gloomy,
    Graceless,
    Grand,
    Greedy,
    Grim,
    Gullible,
    Hateful,
    Haughty,
    Hedonistic,
    Hesitant,
    Hidebound,
    HighHanded,
    Hostile,
    Ignorant,
    Imitative,
    Impatient,
    Impractical,
    Imprudent,
    Impulsive,
    Inconsiderate,
    Incurious,
    Indecisive,
    Indulgent,
    Inert,
    Inhibited,
    Insecure,
    Insensitive,
    Insincere,
    Insulting,
    Intolerant,
    Irascible,
    Irrational,
    Irresponsible,
    Irritable,
    Lazy,
    Libidinous,
    Loquacious,
    Malicious,
    Mannered,
    Mannerless,
    Mawkish,
    Mealymouthed,
    Mechanical,
    Meddlesome,
    Melancholic,
    Meretricious,
    Messy,
    Miserable,
    Miserly,
    Misguided,
    Mistaken,
    MoneyMinded,
    Monstrous,
    Moody,
    Morbid,
    MuddleHeaded,
    Naive,
    Narcissistic,
    Narrow,
    NarrowMinded,
    Natty,
    Negativistic,
    Neglectful,
    Neurotic,
    Nihilistic,
    Obnoxious,
    Obsessive,
    Obvious,
    Odd,
    Offhand,
    OneDimensional,
    OneSided,
    Opinionated,
    Opportunistic,
    Oppressed,
    Outrageous,
    Overimaginative,
    Paranoid,
    Passive,
    Pedantic,
    Perverse,
    Petty,
    Pharissical,
    Phlegmatic,
    Plodding,
    Pompous,
    Possessive,
    PowerHungry,
    Predatory,
    Prejudiced,
    Presumptuous,
    Pretentious,
    Prim,
    Procrastinating,
    Profligate,
    Provocative,
    Pugnacious,
    Puritanical,
    Quirky,
    Reactionary,
    Reactive,
    Regimental,
    Regretful,
    Repentant,
    Repressed,
    Resentful,
    Ridiculous,
    Rigid,
    Ritualistic,
    Rowdy,
    Ruined,
    Sadistic,
    Sanctimonious,
    Scheming,
    Scornful,
    Secretive,
    Sedentary,
    Selfish,
    SelfIndulgent,
    Shallow,
    Shortsighted,
    Shy,
    Silly,
    SingleMinded,
    Sloppy,
    Slow,
    Sly,
    SmallThinking,
    Softheaded,
    Sordid,
    Steely,
    Stiff,
    StrongWilled,
    Stupid,
    Submissive,
    Superficial,
    Superstitious,
    Suspicious,
    Tactless,
    Tasteless,
    Tense,
    Thievish,
    Thoughtless,
    Timid,
    Transparent,
    Treacherous,
    Trendy,
    Troublesome,
    Unappreciative,
    Uncaring,
    Uncharitable,
    Unconvincing,
    Uncooperative,
    Uncreative,
    Uncritical,
    Unctuous,
    Undisciplined,
    Unfriendly,
    Ungrateful,
    Unhealthy,
    Unimaginative,
    Unimpressive,
    Unlovable,
    Unpolished,
    Unprincipled,
    Unrealistic,
    Unreflective,
    Unreliable,
    Unrestrained,
    UnselfCritical,
    Unstable,
    Vacuous,
    Vague,
    Venal,
    Venomous,
    Vindictive,
    Vulnerable,
    Weak,
    WeakWilled,
    WellMeaning,
    Willful,
    Wishful,
    Zany;

    //private final boolean _isGood;
    //private final boolean _isNeutral;
    //private final boolean _isBad;
    private boolean _initialized = false;
    private int _value;
    private String _text = null;

    Trait() {
        this._value = ordinal();
    }

    public boolean isGood() {
        if (_value <= Youthful.val()) {
            return true;
        }
        return false;
    }

    public boolean isNeutral() {
        if (!isGood() && !isBad()) {
            return true;
        }
        return false;
    }

    public boolean isBad() {
        if (_value >= Abrasive.val()) {
            return true;
        }
        return false;
    }

    public int val() {
        return _value;
    }
    public int getValue() {
        return _value;
    }

    public String text() {
        if (_initialized == false) {
            setup();
        }
        return _text;
    }
    public String getText() {
        return text();
    }
    
    private void setup() {
        _initialized = true;
        this._text = toString();
        
        switch (this) {
            case ClearHeaded:
                this._text = "Clear-headed";
                break;
            case FunLoving:
                this._text = "Fun-loving";
                break;
            case GoodNatured:
                this._text = "Good-natured";
                break;
            case HighMinded:
                this._text = "High-minded";
                break;
            case ManySided:
                this._text = "Many-sided";
                break;
            case MultiLeveled:
                this._text = "Multi-leveled";
                break;
            case SelfCritical:
                this._text = "Self-critical";
                break;
            case SelfDefacing:
                this._text = "Self-defacing";
                break;
            case SelfDenying:
                this._text = "Self-denying";
                break;
            case SelfReliant:
                this._text = "Self-reliant";
                break;
            case SelfSufficent:
                this._text = "Self-sufficent";
                break;
            case WellBred:
                this._text = "Well-bred";
                break;
            case WellRead:
                this._text = "Well-read";
                break;
            case WellRounded:
                this._text = "Well-rounded";
                break;
            case BigThinking:
                this._text = "Big-thinking";
                break;
            case HighSpirited:
                this._text = "High-spirited";
                break;
            case OldFashioned:
                this._text = "Old-fashioned";
                break;
            case SelfConscious:
                this._text = "Self-conscious";
                break;
            case EasilyDiscouraged:
                this._text = "Easily Discouraged";
                break;
            case HighHanded:
                this._text = "High-handed";
                break;
            case MoneyMinded:
                this._text = "Money-minded";
                break;
            case MuddleHeaded:
                this._text = "Muddle-headed";
                break;
            case NarrowMinded:
                this._text = "Narrow-minded";
                break;
            case OneDimensional:
                this._text = "One-dimensional";
                break;
            case OneSided:
                this._text = "One-sided";
                break;
            case PowerHungry:
                this._text = "Power-hungry";
                break;
            case SelfIndulgent:
                this._text = "Self-indulgent";
                break;
            case SingleMinded:
                this._text = "Single-minded";
                break;
            case SmallThinking:
                this._text = "Small-thinking";
                break;
            case StrongWilled:
                this._text = "Strong-willed";
                break;
            case UnselfCritical:
                this._text = "Unself-critical";
                break;
            case WeakWilled:
                this._text = "Weak-willed";
                break;
            case WellMeaning:
                this._text = "Well-meaning";
                break;
        }    
    }

    // We don't want to call methods on cachedValues
    // before it's been set so instead we define this
    // size method which just gets the length of the array,
    // but first checks to make sure it exists.
    public static int size() {
        if (Trait.cachedValues == null) {
            Trait.cachedValues = Trait.values();
        }
        return Trait.cachedValues.length;
    }

    private static Trait[] cachedValues = null;
    public static Trait fromInt(int i) {
        if (Trait.cachedValues == null) {
            Trait.cachedValues = Trait.values();
        }
        return Trait.cachedValues[i];
    }

    public static Trait getRandomTrait() {
        // These are the ones that appear for hire
        int start = 0;
        int end = Trait.size()-1;
        return (fromInt(Utils.number(start, end)));
    }
}